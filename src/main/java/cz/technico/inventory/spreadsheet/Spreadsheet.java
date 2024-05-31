package cz.technico.inventory.spreadsheet;

import cz.technico.inventory.constructionwork.ConstructionWork;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spreadsheet")
public class Spreadsheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID uuid;
    private String filename;
    @OneToMany(mappedBy = "spreadsheet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<ConstructionWork> constructionWorks;
}
