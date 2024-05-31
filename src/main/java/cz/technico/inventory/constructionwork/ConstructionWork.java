package cz.technico.inventory.constructionwork;

import com.fasterxml.jackson.databind.JsonNode;
import cz.technico.inventory.spreadsheet.Spreadsheet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "construction_work")
public class ConstructionWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode data;
    @ManyToOne
    private Spreadsheet spreadsheet;
}
