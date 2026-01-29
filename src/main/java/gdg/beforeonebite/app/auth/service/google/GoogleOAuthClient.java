package gdg.beforeonebite.app.auth.service.google;

import gdg.beforeonebite.app.auth.dto.google.GoogleTokenResponse;
import gdg.beforeonebite.app.auth.dto.google.GoogleUserInfoResponse;

public interface GoogleOAuthClient {
    GoogleTokenResponse exchangeCodeForToken(String code);
    GoogleUserInfoResponse getUserInfo(String accessToken);
}
