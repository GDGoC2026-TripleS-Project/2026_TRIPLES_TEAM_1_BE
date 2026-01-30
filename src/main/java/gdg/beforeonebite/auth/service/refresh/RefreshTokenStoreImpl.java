package gdg.beforeonebite.auth.service.refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RefreshTokenStoreImpl implements RefreshTokenStore {

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> refreshRotateScript;

    private String key(Long userId) {
        return "refresh:jti:" + userId;
    }

    @Override
    public void save(Long userId, String jti, long ttlSeconds) {
        redis.opsForValue().set(key(userId), jti, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public long rotate(Long userId, String oldJti, String newJti, long ttlSeconds) {
        Long result = redis.execute(
                refreshRotateScript,
                List.of(key(userId)),
                oldJti,
                newJti,
                String.valueOf(ttlSeconds)
        );
        return result == null ? 0 : result;
    }

    @Override
    public void delete(Long userId) {
        redis.delete(key(userId));
    }
}
