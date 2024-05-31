package cz.technico.inventory.refreshtoken;
public interface RefreshTokenService {
    RefreshToken save(RefreshToken refreshToken);
    void deleteAllByEmail(String email);
    void deleteByToken(String token);
}
