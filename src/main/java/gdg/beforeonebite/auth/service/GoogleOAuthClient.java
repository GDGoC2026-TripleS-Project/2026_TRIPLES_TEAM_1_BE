package gdg.beforeonebite.auth.service;

import gdg.beforeonebite.auth.config.GoogleOAuthProperties;
import gdg.beforeonebite.auth.dto.GoogleTokenResponse;
import gdg.beforeonebite.auth.dto.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private final GoogleOAuthProperties googleOAuthProperties;
    private final WebClient webClient = WebClient.builder().build();

    public GoogleTokenResponse exchangeCodeForToken(String code) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("client_id", googleOAuthProperties.clientId());
        map.add("client_secret", googleOAuthProperties.clientSecret());
        map.add("redirect_uri", googleOAuthProperties.redirectUri());
        map.add("grant_type", "authorization_code");

        return webClient.post()
                .uri(googleOAuthProperties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(map)
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();
    }

    public GoogleUserInfo getUserInfo(String accessToken) {
        return webClient.get()
                .uri(googleOAuthProperties.userinfoUri())
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }
}
