package gdg.beforeonebite.app.auth.controller;

import gdg.beforeonebite.app.auth.dto.TokenReissueResult;
import gdg.beforeonebite.app.auth.service.AuthService;
import gdg.beforeonebite.app.auth.util.CookieUtil;
import gdg.beforeonebite.global.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "인증 API",
        description = "인증 및 토큰 관리 API"
)
public class AuthController {

    private final AuthService authService;

    @Value("${auth.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${auth.cookie.refresh-max-age:604800}")
    private long refreshMaxAgeSeconds;

    @Value("${auth.cookie.same-site:Lax}")
    private String cookieSameSite;

    @PostMapping("/reissue")
    @Operation(
            summary = "AccessToken 재발급",
            description =
                    """
                    HttpOnly RefreshToken 쿠키를 기반으로 새로운 AccessToken을 발급합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "RefreshToken이 유효하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러, 관리자에게 문의")
    })
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
    @Operation(
            summary = "로그아웃",
            description =
                    """
                    RefreshToken을 무효화하고 쿠키를 삭제합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "올바른 RefreshToken이 아님"),
            @ApiResponse(responseCode = "500", description = "서버 에러, 관리자에게 문의")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = CookieUtil.getRefreshToken(request);
        authService.logout(refresh);

        CookieUtil.clearRefreshToken(response, cookieSecure, cookieSameSite);
        return ResponseEntity.noContent().build();
    }

    public record AccessTokenResponse(String accessToken) {
    }
}

