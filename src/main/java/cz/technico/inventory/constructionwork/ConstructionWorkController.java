package cz.technico.inventory.constructionwork;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonPatch;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.flipkart.zjsonpatch.JsonDiff;

import java.util.UUID;

@RestController
@RequestMapping("/construction-work")
@AllArgsConstructor
public class ConstructionWorkController {
    private ConstructionWorkService constructionWorkService;

    @GetMapping("{spid}/{workName}")
    public ResponseEntity<JsonNode> getData(
            @PathVariable UUID spid,
            @PathVariable String workName
    ) {
        return ResponseEntity.ok(constructionWorkService.getBySpidAndName(spid, workName).getData());
    }

    @PutMapping("{spid}/{workName}")
    public ResponseEntity<?> update(
            @PathVariable UUID spid,
            @PathVariable String workName,
            @RequestBody JsonNode updatedData
    ) {
        ConstructionWork cs = constructionWorkService.getBySpidAndName(spid, workName);
        JsonNode oldData = cs.getData();
        JsonNode patch = JsonDiff.asJson(oldData, updatedData);
        cs.setData(JsonPatch.apply(patch, oldData));
        constructionWorkService.save(cs);
        return ResponseEntity.ok().build();
    }
}
