package gdg.beforeonebite.app.auth.service.google;

import gdg.beforeonebite.app.auth.domain.OAuthProvider;
import gdg.beforeonebite.app.auth.domain.User;
import gdg.beforeonebite.app.auth.dto.TokenReissueResult;
import gdg.beforeonebite.app.auth.dto.google.GoogleTokenResponse;
import gdg.beforeonebite.app.auth.dto.google.GoogleUserInfoResponse;
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
public class GoogleOAuthService {

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    private final GoogleOAuthClient googleOAuthClient;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Value("${app.oauth.google.client-id}")
    private String clientId;

    @Value("${app.oauth.google.redirect-uri}")
    private String redirectUri;

    @Value("${app.oauth.google.scope}")
    private String scope;

    @Value("${app.oauth.frontend-redirect-url}")
    private String frontendRedirectUrl;

    @Value("${auth.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${auth.cookie.refresh-max-age:604800}")
    private long refreshMaxAgeSeconds;

    @Value("${auth.cookie.same-site:Lax}")
    private String cookieSameSite;

    public String buildGoogleAuthorizeUrl(HttpServletResponse response) {
        String state = generateState();
        CookieUtil.setOAuthState(response, state, cookieSecure, cookieSameSite);

        return UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("state", state)
                .build()
                .encode()
                .toUriString();
    }

    public String handleCallback(String code, String state, HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.consumeOAuthStateOrThrow(request, response, state, cookieSecure, cookieSameSite);

        GoogleTokenResponse googleToken = googleOAuthClient.exchangeCodeForToken(code);
        GoogleUserInfoResponse userInfo = googleOAuthClient.getUserInfo(googleToken.accessToken());

        User user = userRepository.findByProviderAndProviderId(OAuthProvider.GOOGLE, userInfo.sub())
                .orElseGet(() -> userRepository.save(User.builder()
                        .provider(OAuthProvider.GOOGLE)
                        .providerId(userInfo.sub())
                        .email(userInfo.email())
                        .name(trimName(userInfo.name()))
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
