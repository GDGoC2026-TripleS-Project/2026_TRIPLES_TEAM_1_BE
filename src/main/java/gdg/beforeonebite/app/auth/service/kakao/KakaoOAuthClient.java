package gdg.beforeonebite.app.auth.service.kakao;

import gdg.beforeonebite.app.auth.dto.kakao.KakaoTokenResponse;
import gdg.beforeonebite.app.auth.dto.kakao.KakaoUserInfoResponse;

public interface KakaoOAuthClient {
    KakaoTokenResponse exchangeCodeForToken(String code);
    KakaoUserInfoResponse getUserInfo(String accessToken);
}
