package gdg.beforeonebite.app.auth.controller.kakao;

import gdg.beforeonebite.app.auth.service.kakao.KakaoOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/auth/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String url = kakaoOAuthService.buildKakaoAuthorizeUrl(response);
        response.sendRedirect(url);
    }

    @GetMapping("/oauth2/callback/kakao")
    public void callback(@RequestParam String code,
                         @RequestParam(required = false) String state,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String redirectUrl = kakaoOAuthService.handleCallback(code, state, request, response);
        response.sendRedirect(redirectUrl);
    }
}
