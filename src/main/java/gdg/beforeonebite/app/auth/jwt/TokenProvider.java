package gdg.beforeonebite.app.auth.jwt;

import gdg.beforeonebite.app.auth.domain.AuthUser;
import gdg.beforeonebite.global.exception.BadRequestException;
import gdg.beforeonebite.global.exception.ErrorMessage;
import gdg.beforeonebite.global.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class TokenProvider {

    private static final String ROLE_CLAIM = "role";
    private static final String DELIMITER = ",";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String JTI = "jti";

    private final SecretKey key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidity,
                         @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenValidityTime = accessTokenValidity;
        this.refreshTokenValidityTime = refreshTokenValidity;
    }

    public String createAccessToken(Long userId, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidityTime);

        String rawRole = (role != null && role.startsWith(ROLE_PREFIX))
                ? role.substring(ROLE_PREFIX.length())
                : role;

        return Jwts.builder()
                .subject(userId.toString())
                .claim(ROLE_CLAIM, rawRole)
                .claim(TOKEN_TYPE, ACCESS_TOKEN)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public RefreshIssued createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenValidityTime);
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .subject(userId.toString())
                .claim(TOKEN_TYPE, REFRESH_TOKEN)
                .claim(JTI, jti)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();

        long ttlSeconds = Math.max(1L, expiration.toInstant().getEpochSecond() - Instant.now().getEpochSecond());
        return new RefreshIssued(token, jti, ttlSeconds);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaim(token);
        String tokenType = claims.get(TOKEN_TYPE, String.class);

        if (REFRESH_TOKEN.equals(tokenType)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_REFRESH_TOKEN);
        }

        Long userId = Long.parseLong(claims.getSubject());
        String roleClaim = claims.get(ROLE_CLAIM, String.class);

        List<SimpleGrantedAuthority> authorities =
                Arrays.stream(StringUtils.hasText(roleClaim) ? roleClaim.split(DELIMITER) : new String[0])
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .map(TokenProvider::toRole)
                        .filter(Objects::nonNull)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        AuthUser principal = new AuthUser(userId, roleClaim);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }
        return null;
    }

    public Claims parseClaim(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorMessage.EXPIRED_TOKEN);
        } catch (SecurityException e) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }
    }

    private static String toRole(String role) {
        if (!StringUtils.hasText(role)) {
            return null;
        }

        String trimmed = role.trim();
        return trimmed.startsWith(ROLE_PREFIX) ? trimmed : ROLE_PREFIX + trimmed;
    }

    public RefreshClaims parseRefreshClaims(String token) {
        Claims claims = parseClaim(token);
        String tokenType = claims.get(TOKEN_TYPE, String.class);

        if (!REFRESH_TOKEN.equals(tokenType)) {
            throw new BadRequestException(ErrorMessage.IS_NOT_REFRESH_TOKEN);
        }

        Long userId = Long.parseLong(claims.getSubject());
        String jti = claims.get(JTI, String.class);
        if (!StringUtils.hasText(jti)) {
            throw new BadRequestException(ErrorMessage.NO_JTI_IN_TOKEN);
        }

        long ttlSeconds = Math.max(1L, claims.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond());
        return new RefreshClaims(userId, jti, ttlSeconds);
    }

    public Claims parseClaimAllowExpired(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (SecurityException e) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorMessage.INVALID_TOKEN);
        }
    }

    public Long getUserIdFromRefreshAllowExpired(String token) {
        Claims claims = parseClaimAllowExpired(token);

        String tokenType = claims.get(TOKEN_TYPE, String.class);
        if (!REFRESH_TOKEN.equals(tokenType)) {
            throw new BadRequestException(ErrorMessage.IS_NOT_REFRESH_TOKEN);
        }
        return Long.parseLong(claims.getSubject());
    }

    public record RefreshIssued(String token, String jti, long ttlSeconds) {
    }

    public record RefreshClaims(Long userId, String jti, long ttlSeconds) {
    }
}
