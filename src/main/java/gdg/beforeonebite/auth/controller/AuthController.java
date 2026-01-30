package gdg.beforeonebite.auth.controller;

import gdg.beforeonebite.auth.dto.TokenReissueResult;
import gdg.beforeonebite.auth.service.AuthService;
import gdg.beforeonebite.auth.util.CookieUtil;
import gdg.beforeonebite.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${auth.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${auth.cookie.refresh-max-age:604800}")
    private long refreshMaxAgeSeconds;

    @Value("${auth.cookie.same-site:Lax}")
    private String cookieSameSite;

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refresh = CookieUtil.getRefreshToken(request);
            TokenReissueResult result = authService.reissue(refresh);

            CookieUtil.setRefreshToken(response, result.newRefreshToken(), cookieSecure, refreshMaxAgeSeconds, cookieSameSite);
            return ResponseEntity.ok(new AccessTokenResponse(result.accessToken()));
        } catch (UnauthorizedException | IllegalArgumentException e) {
            CookieUtil.clearRefreshToken(response, cookieSecure, cookieSameSite);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = CookieUtil.getRefreshToken(request);
        authService.logout(refresh);

        CookieUtil.clearRefreshToken(response, cookieSecure, cookieSameSite);
        return ResponseEntity.noContent().build();
    }

    public record AccessTokenResponse(String accessToken) {
    }
}
