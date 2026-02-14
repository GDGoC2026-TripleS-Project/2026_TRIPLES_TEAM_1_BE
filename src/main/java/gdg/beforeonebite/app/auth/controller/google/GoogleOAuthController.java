package gdg.beforeonebite.app.auth.controller.google;

import gdg.beforeonebite.app.auth.service.google.GoogleOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "OAuth - Google", description = "구글 OAuth 로그인 API")
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/auth/google")
    @Operation(
            summary = "Google 로그인 시작",
            description = "Google OAuth 인증 페이지로 리다이렉트합니다."
    )
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String googleUrl = googleOAuthService.buildGoogleAuthorizeUrl(response);
        response.sendRedirect(googleUrl);
    }

    @GetMapping("/oauth2/callback/google")
    @Operation(
            summary = "Google OAuth 콜백",
            description = "Google 인증 완료 후 callback을 처리하고 프론트엔드로 리다이렉트합니다."
    )
    public void callback(@RequestParam String code,
                         @RequestParam(required = false) String state,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String redirectUrl = googleOAuthService.handleCallback(code, state, request, response);
        response.sendRedirect(redirectUrl);
    }
}
