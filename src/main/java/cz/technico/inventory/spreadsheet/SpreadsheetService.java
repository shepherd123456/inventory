package cz.technico.inventory.spreadsheet;

import java.util.List;
import java.util.UUID;

public interface SpreadsheetService {
    Spreadsheet save(Spreadsheet spreadsheet);
    Spreadsheet getBySpid(UUID uuid);
    List<Object[]> getSpidAndWorkNames(String filename);
}
