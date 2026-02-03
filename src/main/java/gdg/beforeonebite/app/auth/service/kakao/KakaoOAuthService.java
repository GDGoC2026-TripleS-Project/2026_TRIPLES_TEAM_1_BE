package gdg.beforeonebite.app.auth.service.kakao;

import gdg.beforeonebite.app.auth.domain.OAuthProvider;
import gdg.beforeonebite.app.auth.domain.User;
import gdg.beforeonebite.app.auth.dto.TokenReissueResult;
import gdg.beforeonebite.app.auth.dto.kakao.KakaoTokenResponse;
import gdg.beforeonebite.app.auth.dto.kakao.KakaoUserInfoResponse;
import gdg.beforeonebite.app.auth.repository.UserRepository;
import gdg.beforeonebite.app.auth.service.AuthService;
import gdg.beforeonebite.app.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Value("${app.oauth.kakao.client-id}")
    private String clientId;

    @Value("${app.oauth.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${app.oauth.kakao.scope:}")
    private String scope;

    @Value("${app.oauth.frontend-redirect-url}")
    private String frontendRedirectUrl;

    @Value("${auth.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${auth.cookie.refresh-max-age:604800}")
    private long refreshMaxAgeSeconds;

    @Value("${auth.cookie.same-site:Lax}")
    private String cookieSameSite;

    public String buildKakaoAuthorizeUrl(HttpServletResponse response) {
        String state = generateState();
        CookieUtil.setOAuthState(response, state, cookieSecure, cookieSameSite);

        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(KAKAO_AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state);

        if (scope != null && !scope.isBlank()) {
            b.queryParam("scope", scope);
        }

        return b.build().encode().toUriString();
    }

    public String handleCallback(String code, String state, HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.consumeOAuthStateOrThrow(request, response, state, cookieSecure, cookieSameSite);

        KakaoTokenResponse token = kakaoOAuthClient.exchangeCodeForToken(code);
        KakaoUserInfoResponse userInfo = kakaoOAuthClient.getUserInfo(token.accessToken());

        String providerId = String.valueOf(userInfo.id());
        String email = userInfo.kakaoAccount() != null ? userInfo.kakaoAccount().email() : null;
        String nickname = (userInfo.kakaoAccount() != null && userInfo.kakaoAccount().profile() != null)
                ? userInfo.kakaoAccount().profile().nickname()
                : null;

        User user = userRepository.findByProviderAndProviderId(OAuthProvider.KAKAO, providerId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .provider(OAuthProvider.KAKAO)
                        .providerId(providerId)
                        .email(email)
                        .name(trimName(nickname))
                        .build()));

        TokenReissueResult session = authService.issueSession(user.getId());
        CookieUtil.setRefreshToken(response, session.newRefreshToken(), cookieSecure, refreshMaxAgeSeconds, cookieSameSite);

        return frontendRedirectUrl;
    }

    private String trimName(String name) {
        if (name == null) {
            return null;
        }

        return name.length() > 10 ? name.substring(0, 10) : name;
    }

    private String generateState() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
