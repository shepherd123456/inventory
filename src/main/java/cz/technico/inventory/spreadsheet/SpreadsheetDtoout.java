package cz.technico.inventory.spreadsheet;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public class SpreadsheetDtoout {
    public UUID spid;
    public Set<String> workNames;
}
