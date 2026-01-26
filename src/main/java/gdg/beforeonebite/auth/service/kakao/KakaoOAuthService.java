package gdg.beforeonebite.auth.service.kakao;

import gdg.beforeonebite.auth.domain.OAuthProvider;
import gdg.beforeonebite.auth.domain.User;
import gdg.beforeonebite.auth.dto.kakao.KakaoTokenResponse;
import gdg.beforeonebite.auth.dto.kakao.KakaoUserInfoResponse;
import gdg.beforeonebite.auth.jwt.TokenProvider;
import gdg.beforeonebite.auth.repository.UserRepository;
import gdg.beforeonebite.auth.util.CookieUtil;
import gdg.beforeonebite.exception.ErrorMessage;
import gdg.beforeonebite.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

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

    public String buildKakaoAuthorizeUrl(HttpServletResponse response) {
        String state = generateState();
        CookieUtil.setOAuthState(response, state, cookieSecure);

        String encodedScope = (scope == null || scope.isBlank()) ? null : URLEncoder.encode(scope, StandardCharsets.UTF_8);

        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(KAKAO_AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state);

        if (encodedScope != null) {
            b.queryParam("scope", encodedScope);
        }

        return b.build(true).toUriString();
    }

    public String handleCallback(String code, String state, HttpServletRequest request, HttpServletResponse response) {
        String stateCookie = CookieUtil.getOAuthState(request);
        if (state == null || stateCookie == null || !state.equals(stateCookie)) {
            CookieUtil.clearOAuthState(response, cookieSecure);
            throw new UnauthorizedException(ErrorMessage.OAUTH_STATE_VALIDATION_FAILED);
        }
        CookieUtil.clearOAuthState(response, cookieSecure);

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

        String refreshToken = tokenProvider.createRefreshToken(user.getId());
        CookieUtil.setRefreshToken(response, refreshToken, cookieSecure, refreshMaxAgeSeconds);

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
