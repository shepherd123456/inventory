package cz.technico.inventory.user;

import lombok.AllArgsConstructor;

import cz.technico.inventory.role.Role;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserDtoout {
    public String email;
    public List<String> roles;

    public UserDtoout(User user){
        email = user.getEmail();
        roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
    }
}