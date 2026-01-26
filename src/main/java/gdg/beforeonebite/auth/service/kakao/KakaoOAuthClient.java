package gdg.beforeonebite.auth.service.kakao;

import gdg.beforeonebite.auth.dto.kakao.KakaoTokenResponse;
import gdg.beforeonebite.auth.dto.kakao.KakaoUserInfoResponse;

public interface KakaoOAuthClient {
    KakaoTokenResponse exchangeCodeForToken(String code);
    KakaoUserInfoResponse getUserInfo(String accessToken);
}
