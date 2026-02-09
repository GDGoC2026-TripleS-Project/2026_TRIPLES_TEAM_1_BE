package gdg.beforeonebite.global.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    // Auth
    INVALID_REFRESH_TOKEN("유효하지 않은 토큰입니다."),
    INVALID_SESSION("세션이 만료되었습니다."),
    IS_NOT_REFRESH_TOKEN("리프레시 토큰이 아닙니다."),
    OAUTH_TOKEN_EXCHANGE_FAILED("토큰 교환에 실패했습니다."),
    OAUTH_PROFILE_FETCH_FAILED("사용자 정보를 불러오지 못했습니다."),
    OAUTH_UNAVAILABLE("OAuth 서버와 통신할 수 없습니다."),
    OAUTH_STATE_VALIDATION_FAILED("OAuth state 검증에 실패했습니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    NO_JTI_IN_TOKEN("refresh token에 jti가 없습니다."),
    USER_NOT_EXIST("유저가 존재하지 않습니다."),

    // Food
    INVALID_SEARCH_KEYWORD("검색어는 필수입니다."),
    FOOD_NOT_EXIST("일치하는 음식이 없습니다."),
    BRAND_NOT_EXIST("일치하는 브랜드가 없습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
