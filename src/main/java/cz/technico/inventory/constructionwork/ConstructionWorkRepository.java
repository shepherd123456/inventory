package cz.technico.inventory.constructionwork;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ConstructionWorkRepository extends JpaRepository<ConstructionWork, Long> {
    @Query("SELECT cw FROM ConstructionWork cw WHERE cw.spreadsheet.uuid = :spid AND cw.name = :name")
    ConstructionWork getBySpidAndName(@Param("spid") UUID spid , @Param("name") String name);
}
