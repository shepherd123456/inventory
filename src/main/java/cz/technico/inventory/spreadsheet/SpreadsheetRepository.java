package cz.technico.inventory.spreadsheet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpreadsheetRepository extends JpaRepository<Spreadsheet, Long> {
    Spreadsheet findByUuid(UUID uuid);
    @Query("SELECT sp.uuid, cw.name FROM Spreadsheet sp JOIN sp.constructionWorks cw WHERE LOWER(sp.filename) LIKE CONCAT('%', LOWER(:filename), '%')")
    List<Object[]> getSpidAndWorkNames(@Param("filename") String filename);
}
