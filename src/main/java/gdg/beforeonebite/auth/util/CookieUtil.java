package gdg.beforeonebite.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final String OAUTH_STATE = "OAUTH_STATE";
    private static final int STATE_MAX_AGE_SECONDS = 300; // 5분

    public static void setOAuthState(HttpServletResponse response, String state) {
        Cookie cookie = new Cookie(OAUTH_STATE, state);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(STATE_MAX_AGE_SECONDS);
        response.addCookie(cookie);
    }

    public static String getOAuthState(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (OAUTH_STATE.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    public static void clearOAuthState(HttpServletResponse response) {
        Cookie cookie = new Cookie(OAUTH_STATE, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
