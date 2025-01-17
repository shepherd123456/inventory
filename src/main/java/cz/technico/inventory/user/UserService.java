package cz.technico.inventory.user;

public interface UserService {
    User save(User user);
    User findByEmail(String email);
    User findByRefreshToken(String token);
    void appendRole(String email, String roleName);
}
