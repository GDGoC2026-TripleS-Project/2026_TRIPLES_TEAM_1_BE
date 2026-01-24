package gdg.beforeonebite.auth.service;

import gdg.beforeonebite.auth.dto.GoogleTokenResponse;
import gdg.beforeonebite.auth.dto.GoogleUserInfoResponse;
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
public class GoogleOAuthClientImpl implements GoogleOAuthClient {

    private final RestTemplate restTemplate;

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://openidconnect.googleapis.com/v1/userinfo";

    @Value("${app.oauth.google.client-id}")
    private String clientId;

    @Value("${app.oauth.google.client-secret}")
    private String clientSecret;

    @Value("${app.oauth.google.redirect-uri}")
    private String redirectUri;

    @Override
    public GoogleTokenResponse exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, GoogleTokenResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalArgumentException("교환 실패");
            }

            return response.getBody();
        } catch (RestClientException e) {
            throw new IllegalArgumentException("교환 실패", e);
        }
    }

    @Override
    public GoogleUserInfoResponse getUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

        try {
            ResponseEntity<GoogleUserInfoResponse> exchanged = restTemplate.exchange(USERINFO_URL, HttpMethod.GET, httpEntity, GoogleUserInfoResponse.class);

            if (!exchanged.getStatusCode().is2xxSuccessful() || exchanged.getBody() == null) {
                throw new IllegalArgumentException("사용자 프로필 불러오기 실패");
            }

            return exchanged.getBody();
        } catch (RestClientException e) {
            throw new IllegalArgumentException("사용자 프로필 불러오기 실패", e);
        }
    }
}
