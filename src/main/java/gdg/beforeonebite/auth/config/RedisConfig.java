package gdg.beforeonebite.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }

    @Bean
    public DefaultRedisScript<Long> refreshRotateScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                "local cur = redis.call('GET', KEYS[1]) " +
                        "if (not cur) then return 0 end " +
                        "if (cur ~= ARGV[1]) then return -1 end " +
                        "redis.call('SETEX', KEYS[1], ARGV[3], ARGV[2]) " +
                        "return 1 "
        );
        return script;
    }
}
