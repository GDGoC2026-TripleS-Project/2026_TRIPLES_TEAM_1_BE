package gdg.beforeonebite.auth.service.kakao;

import gdg.beforeonebite.auth.dto.kakao.KakaoTokenResponse;
import gdg.beforeonebite.auth.dto.kakao.KakaoUserInfoResponse;
import gdg.beforeonebite.exception.ErrorMessage;
import gdg.beforeonebite.exception.ExternalServiceException;
import gdg.beforeonebite.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClientImpl implements KakaoOAuthClient {

    private final RestTemplate restTemplate;

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USERINFO_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${app.oauth.kakao.client-id}")
    private String clientId;

    @Value("${app.oauth.kakao.client-secret:}")
    private String clientSecret;

    @Value("${app.oauth.kakao.redirect-uri}")
    private String redirectUri;

    @Override
    public KakaoTokenResponse exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        if (clientSecret != null && !clientSecret.isBlank()) {
            body.add("client_secret", clientSecret);
        }

        try {
            ResponseEntity<KakaoTokenResponse> exchange = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, new HttpEntity<>(body, headers), KakaoTokenResponse.class);

            if (!exchange.getStatusCode().is2xxSuccessful() || exchange.getBody() == null) {
                throw new UnauthorizedException(ErrorMessage.OAUTH_TOKEN_EXCHANGE_FAILED);
            }

            return exchange.getBody();
        } catch (RestClientException e) {
            throw new ExternalServiceException(ErrorMessage.OAUTH_UNAVAILABLE.getMessage(), e);
        }
    }

    @Override
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<KakaoUserInfoResponse> exchange = restTemplate.exchange(USERINFO_URL, HttpMethod.GET, new HttpEntity<>(headers), KakaoUserInfoResponse.class);

            if (!exchange.getStatusCode().is2xxSuccessful() || exchange.getBody() == null) {
                throw new UnauthorizedException(ErrorMessage.OAUTH_PROFILE_FETCH_FAILED);
            }

            return exchange.getBody();
        } catch (RestClientException e) {
            throw new ExternalServiceException(ErrorMessage.OAUTH_UNAVAILABLE.getMessage(), e);
        }
    }
}
