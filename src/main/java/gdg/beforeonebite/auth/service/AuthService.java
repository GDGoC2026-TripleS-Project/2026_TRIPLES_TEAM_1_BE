package gdg.beforeonebite.auth.service;

import gdg.beforeonebite.auth.dto.TokenReissueResult;

public interface AuthService {
    TokenReissueResult issueSession(Long userId);

    TokenReissueResult reissue(String refreshToken);

    void logout(String refreshToken);
}
