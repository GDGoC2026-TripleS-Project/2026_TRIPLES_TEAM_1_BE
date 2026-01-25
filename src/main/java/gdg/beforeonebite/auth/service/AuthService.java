package gdg.beforeonebite.auth.service;

import gdg.beforeonebite.auth.dto.TokenReissueResult;
import gdg.beforeonebite.auth.exception.BadRequestException;
import gdg.beforeonebite.auth.exception.ErrorMessage;
import gdg.beforeonebite.auth.exception.UnauthorizedException;
import gdg.beforeonebite.auth.jwt.TokenProvider;
import gdg.beforeonebite.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public TokenReissueResult reissue(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) || !tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_REFRESH_TOKEN);
        }

        Long userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);

        if (!userRepository.existsById(userId)) {
            throw new UnauthorizedException(ErrorMessage.INVALID_SESSION);
        }

        String newAccess = tokenProvider.createAccessToken(userId, DEFAULT_ROLE);
        String newRefresh = tokenProvider.createRefreshToken(userId);

        return new TokenReissueResult(newAccess, newRefresh);
    }
}

