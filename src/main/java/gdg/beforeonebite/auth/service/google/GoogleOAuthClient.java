package gdg.beforeonebite.auth.service.google;

import gdg.beforeonebite.auth.dto.google.GoogleTokenResponse;
import gdg.beforeonebite.auth.dto.google.GoogleUserInfoResponse;

public interface GoogleOAuthClient {
    GoogleTokenResponse exchangeCodeForToken(String code);
    GoogleUserInfoResponse getUserInfo(String accessToken);
}
