package gdg.beforeonebite.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    private static final String OAUTH_STATE = "OAUTH_STATE";
    private static final int STATE_MAX_AGE_SECONDS = 300;
    private static final String SAME_SITE = "LAX";

    private static final String REFRESH_TOKEN = "sid";

    public static void setOAuthState(HttpServletResponse response, String state, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(OAUTH_STATE, state)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(SAME_SITE)
                .maxAge(STATE_MAX_AGE_SECONDS)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static String getOAuthState(HttpServletRequest request) {
        return getCookieValue(request, OAUTH_STATE);
    }

    public static void clearOAuthState(HttpServletResponse response, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(OAUTH_STATE, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(SAME_SITE)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void setRefreshToken(HttpServletResponse response, String token, boolean secure, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(SAME_SITE)
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static String getRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, REFRESH_TOKEN);
    }

    public static void clearRefreshToken(HttpServletResponse response, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(SAME_SITE)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }

        return null;
    }
}
