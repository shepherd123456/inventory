package cz.technico.inventory.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.technico.inventory.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roles") // this is causing circular reference in json serialization
    @JsonIgnore
    private List<User> users;
}
