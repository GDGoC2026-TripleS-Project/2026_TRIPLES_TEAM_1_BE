package gdg.beforeonebite.auth.service;

import gdg.beforeonebite.auth.dto.TokenReissueResult;
import gdg.beforeonebite.auth.jwt.TokenProvider;
import gdg.beforeonebite.auth.service.refresh.RefreshTokenStore;
import gdg.beforeonebite.exception.BadRequestException;
import gdg.beforeonebite.exception.ErrorMessage;
import gdg.beforeonebite.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final TokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    public TokenReissueResult issueSession(Long userId) {
        TokenProvider.RefreshIssued refresh = tokenProvider.createRefreshToken(userId);
        refreshTokenStore.save(userId, refresh.jti(), refresh.ttlSeconds());

        String access = tokenProvider.createAccessToken(userId, DEFAULT_ROLE);
        return new TokenReissueResult(access, refresh.token());
    }

    @Override
    public TokenReissueResult reissue(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_REFRESH_TOKEN);
        }

        TokenProvider.RefreshClaims old = tokenProvider.parseRefreshClaims(refreshToken);
        TokenProvider.RefreshIssued fresh = tokenProvider.createRefreshToken(old.userId());

        long rotated = refreshTokenStore.rotate(old.userId(), old.jti(), fresh.jti(), fresh.ttlSeconds());

        if (rotated == 0) {
            throw new UnauthorizedException(ErrorMessage.INVALID_SESSION);
        }
        if (rotated == -1) {
            refreshTokenStore.delete(old.userId());
            throw new UnauthorizedException(ErrorMessage.INVALID_SESSION);
        }

        String newAccess = tokenProvider.createAccessToken(old.userId(), DEFAULT_ROLE);
        return new TokenReissueResult(newAccess, fresh.token());
    }

    @Override
    public void logout(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }

        try {
            Long userId = tokenProvider.getUserIdFromRefreshAllowExpired(refreshToken);
            refreshTokenStore.delete(userId);
        } catch (UnauthorizedException e) {
            log.debug("Logout skipped: {}", e.getMessage());
        } catch (BadRequestException e) {
            log.debug("Logout skipped (bad token): {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Logout failed unexpectedly", e);
        }
    }
}
