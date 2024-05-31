package cz.technico.inventory.spreadsheet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.technico.inventory.constructionwork.ConstructionWork;
import cz.technico.inventory.constructionwork.ConstructionWorkService;
import lombok.AllArgsConstructor;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static cz.technico.inventory.InventoryApplication.*;

@RestController
@RequestMapping("/spreadsheet")
@AllArgsConstructor
public class SpreadsheetController {
    private ObjectMapper objectMapper;
    private SpreadsheetService spreadsheetService;
    private ConstructionWorkService constructionWorkService;

    @PostMapping
    public ResponseEntity<SpreadsheetDtoout> upload(@RequestPart("file") MultipartFile spreadsheet) throws IOException {
        Path root = Files.createDirectories(SPREADSHEET_ROOT.resolve("in"));
        String originalFilename = spreadsheet.getOriginalFilename();
        int dotIndex = originalFilename.lastIndexOf(".");
        String extensionPart = originalFilename.substring(dotIndex + 1);
        String filenamePart = originalFilename.substring(0, dotIndex);
        String filename = filenamePart + "_" + LocalDateTime.now().format(DATETIME_FORMAT) + "." + extensionPart;
        Path path = root.resolve(filename);
        spreadsheet.transferTo(path);
        Spreadsheet sp = spreadsheetService.save(new Spreadsheet(
                null,
                UUID.randomUUID(),
                path.toString(),
                null
        ));

        Set<String> workNames;
        List<ConstructionWork> works = new ArrayList<>();
        try (InputStream is = new FileInputStream(path.toFile());
             ReadableWorkbook wb = new ReadableWorkbook(is)) {
            Map<String, JsonNode> table = parse(wb);
            workNames = table.keySet();
            for (Map.Entry<String, JsonNode> entry : table.entrySet()) {
                works.add(new ConstructionWork(
                        null,
                        entry.getKey(),
                        entry.getValue(),
                        sp
                ));
            }
        }
        constructionWorkService.saveAll(works);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SpreadsheetDtoout(sp.getUuid(), workNames));
    }

    @GetMapping("{filename}")
    public ResponseEntity<SpreadsheetDtoout> load(@PathVariable String filename) {
        List<Object[]> result = spreadsheetService.getSpidAndWorkNames(filename);
        try {
            boolean allSame = true;
            UUID spid = (UUID) result.get(0)[0];
            for (Object[] row : result) {
                UUID uuid = (UUID) row[0];
                if (!uuid.equals(spid)) {
                    allSame = false;
                    break;
                }
            }
            if (!allSame) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else {
                Set<String> workNames = new HashSet<>();
                for (Object[] row : result) {
                    workNames.add((String) row[1]);
                }
                return ResponseEntity.ok(new SpreadsheetDtoout(spid, workNames));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("download/{spid}")
    public ResponseEntity<Resource> download(@PathVariable String spid) throws IOException {
        Path root = Files.createDirectories(SPREADSHEET_ROOT.resolve("out"));
        Spreadsheet sp = spreadsheetService.getBySpid(UUID.fromString(spid));
        String originalFilename = Path.of(sp.getFilename()).getFileName().toString();
        int lastUnderscoreIndex = originalFilename.lastIndexOf('_');
        int secondLastUnderscoreIndex = originalFilename.lastIndexOf('_', lastUnderscoreIndex - 1);
        String filenamePart = originalFilename.substring(0, secondLastUnderscoreIndex);
        int dotIndex = originalFilename.lastIndexOf(".");
        String extensionPart = originalFilename.substring(dotIndex + 1);
        String filename = filenamePart + '_' + sp.getUuid() + '.' + extensionPart;
        File file = root.resolve(filename).toFile();

        try(FileOutputStream os = new FileOutputStream(file);
            Workbook wb = new Workbook(os, "Technico - inventory", "1.0")) {
            for(ConstructionWork cw : sp.getConstructionWorks()){
                Worksheet ws = wb.newWorksheet(cw.getName());
                Map<String, Object> parsedJson = extract(cw.getData());
                List<String> headers = (List<String>) parsedJson.get("headers");
                for(int i = 0; i < headers.size(); i++){
                    ws.value(0, i, headers.get(i));
                }
                List<Map<String, String>> rows = (List<Map<String, String>>) parsedJson.get("data");
                for(int i = 0; i < rows.size(); i++) {
                    for(Map.Entry<String, String> cell : rows.get(i).entrySet()){
                        String key = cell.getKey();
                        int columnIndex = headers.indexOf(key);
                        ws.value(i + 1, columnIndex, cell.getValue());
                    }
                }
            }
            wb.finish();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    private Map<String, JsonNode> parse(ReadableWorkbook wb) {
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        Map<String, Set<String>> headers = new HashMap<>();
        wb.getSheets().forEach(sh -> {
            Row firstRow;
            try {
                firstRow = sh.read().get(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<String> columnNames = new ArrayList<>();
            int discIndex = -1;
            for (int i = 0; i < firstRow.getCellCount(); i++) {
                String value = firstRow.getCellText(i);
                columnNames.add(value);
                if (SPREADSHEET_DISCRIMINANT.equals(value)) {
                    discIndex = i;
                }
            }
            if (discIndex == -1) {
                throw new RuntimeException("Didn't find discriminant [" + SPREADSHEET_DISCRIMINANT + "] of the columns in sheet [" + sh.getName() + "]");
            }
            int finalDiscIndex = discIndex;
            try {
                sh.openStream().skip(1).forEach(r -> {
                    String discValue = r.getCellText(finalDiscIndex);
                    if (discValue.isEmpty()) {
                        discValue = "Ostatní";
                    }
                    Map<String, String> cells = new HashMap<>();
                    for (int i = 0; i < columnNames.size(); i++) {
                        String key = columnNames.get(i);
                        String value = r.getCellText(i);
                        if (!value.isEmpty()) {
                            cells.put(key, value);
                            headers.computeIfAbsent(discValue, k -> new HashSet<>()).add(key);
                        }
                    }
                    data.computeIfAbsent(discValue, k -> new ArrayList<>()).add(cells);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Map<String, JsonNode> result = new HashMap<>();
        for (Map.Entry<String, List<Map<String, String>>> entry : data.entrySet()) {
            Map<String, Object> json = new HashMap<>();
            String id = entry.getKey();
            json.put("headers", new ArrayList<>(headers.get(id)));
            json.put("data", entry.getValue());
            result.put(id, objectMapper.valueToTree(json));
        }
        return result;
    }

    private Map<String, Object> extract(JsonNode json) {
        Map<String, Object> result = new HashMap<>();
        JsonNode headersNode = json.get("headers");
        List<String> headers = new ArrayList<>();
        if (headersNode != null && headersNode.isArray()) {
            for (JsonNode header : headersNode) {
                headers.add(header.asText());
            }
        }
        result.put("headers", headers);
        JsonNode dataNode = json.get("data");
        List<Map<String, String>> data = new ArrayList<>();
        if (dataNode != null && dataNode.isArray()) {
            for (JsonNode entry : dataNode) {
                Map<String, String> map = new HashMap<>();
                entry.fields().forEachRemaining(kv -> {
                    map.put(kv.getKey(), kv.getValue().asText());
                });
                data.add(map);
            }
        }
        result.put("data", data);
        return result;
    }
}
