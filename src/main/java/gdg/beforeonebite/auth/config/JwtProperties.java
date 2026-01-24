package gdg.beforeonebite.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenValidityInMilliseconds,
        long refreshTokenValidityInMilliseconds
) { }
