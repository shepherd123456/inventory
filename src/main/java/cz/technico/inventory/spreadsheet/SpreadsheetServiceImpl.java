package cz.technico.inventory.spreadsheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SpreadsheetServiceImpl implements SpreadsheetService{
    @Autowired
    public SpreadsheetRepository spreadsheetRepository;
    @Override
    public Spreadsheet save(Spreadsheet spreadsheet) {
        return spreadsheetRepository.save(spreadsheet);
    }

    @Override
    public Spreadsheet getBySpid(UUID uuid) {
        return spreadsheetRepository.findByUuid(uuid);
    }

    @Override
    public List<Object[]> getSpidAndWorkNames(String filename) {
        return spreadsheetRepository.getSpidAndWorkNames(filename);
    }
}
