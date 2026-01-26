package gdg.beforeonebite.auth.service;

import gdg.beforeonebite.auth.dto.GoogleTokenResponse;
import gdg.beforeonebite.auth.dto.GoogleUserInfoResponse;

public interface GoogleOAuthClient {
    GoogleTokenResponse exchangeCodeForToken(String code);
    GoogleUserInfoResponse getUserInfo(String accessToken);
}
