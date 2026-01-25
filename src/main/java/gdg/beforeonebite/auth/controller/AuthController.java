package gdg.beforeonebite.auth.controller;

import gdg.beforeonebite.auth.jwt.TokenProvider;
import gdg.beforeonebite.auth.repository.UserRepository;
import gdg.beforeonebite.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private static final String DEFAULT_ROLE = "USER";

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Value("${auth.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${auth.cookie.refresh-max-age:604800}")
    private long refreshMaxAgeSeconds;

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = CookieUtil.getRefreshToken(request);

        if (refresh == null || refresh.isBlank() || !tokenProvider.validateToken(refresh)) {
            CookieUtil.clearRefreshToken(response, cookieSecure);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId;

        try {
            userId = tokenProvider.getUserIdFromRefreshToken(refresh);
        } catch (IllegalArgumentException e) {
            CookieUtil.clearRefreshToken(response, cookieSecure);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!userRepository.existsById(userId)) {
            CookieUtil.clearRefreshToken(response, cookieSecure);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = tokenProvider.createAccessToken(userId, DEFAULT_ROLE);

        String newRefresh = tokenProvider.createRefreshToken(userId);
        CookieUtil.setRefreshToken(response, newRefresh, cookieSecure, refreshMaxAgeSeconds);

        return ResponseEntity.ok(new AccessTokenResponse(accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        CookieUtil.clearRefreshToken(response, cookieSecure);
        return ResponseEntity.noContent().build();
    }

    public record AccessTokenResponse(String accessToken) {}
}

