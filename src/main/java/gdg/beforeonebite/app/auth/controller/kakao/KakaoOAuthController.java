package gdg.beforeonebite.app.auth.controller.kakao;

import gdg.beforeonebite.app.auth.service.kakao.KakaoOAuthService;
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
@Tag(name = "OAuth - Kakao", description = "카카오 OAuth 로그인 API")
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/auth/kakao")
    @Operation(
            summary = "Kakao 로그인 시작",
            description = "카카오 OAuth 인증 페이지로 리다이렉트합니다."
    )
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String url = kakaoOAuthService.buildKakaoAuthorizeUrl(response);
        response.sendRedirect(url);
    }

    @GetMapping("/oauth2/callback/kakao")
    @Operation(
            summary = "Kakao OAuth 콜백",
            description = "카카오 인증 완료 후 callback을 처리하고 프론트엔드로 리다이렉트합니다."
    )
    public void callback(@RequestParam String code,
                         @RequestParam(required = false) String state,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String redirectUrl = kakaoOAuthService.handleCallback(code, state, request, response);
        response.sendRedirect(redirectUrl);
    }
}
