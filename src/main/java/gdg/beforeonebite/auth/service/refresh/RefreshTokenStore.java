package gdg.beforeonebite.auth.service.refresh;

public interface RefreshTokenStore {
    void save(Long userId, String jti, long ttlSeconds);

    long rotate(Long userId, String oldJti, String newJti, long ttlSeconds);

    void delete(Long userId);
}
