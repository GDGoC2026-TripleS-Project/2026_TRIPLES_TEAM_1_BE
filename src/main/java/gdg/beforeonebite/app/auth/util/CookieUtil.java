package gdg.beforeonebite.app.auth.util;

import gdg.beforeonebite.exception.ErrorMessage;
import gdg.beforeonebite.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    private static final String OAUTH_STATE = "OAUTH_STATE";
    private static final int STATE_MAX_AGE_SECONDS = 300;
    private static final String REFRESH_TOKEN = "sid";

    public static void setOAuthState(HttpServletResponse response, String state, boolean secure, String sameSite) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(OAUTH_STATE, state)
                .httpOnly(true)
                .secure(fixSecure(secure, sameSite))
                .path("/")
                .sameSite(sameSite)
                .maxAge(STATE_MAX_AGE_SECONDS)
                .build().toString());
    }

    public static String getOAuthState(HttpServletRequest request) {
        return getCookieValue(request, OAUTH_STATE);
    }

    public static void clearOAuthState(HttpServletResponse response, boolean secure, String sameSite) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(OAUTH_STATE, "")
                .httpOnly(true)
                .secure(fixSecure(secure, sameSite))
                .path("/")
                .sameSite(sameSite)
                .maxAge(0)
                .build().toString());
    }

    public static void setRefreshToken(HttpServletResponse response, String token, boolean secure, long maxAgeSeconds, String sameSite) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(REFRESH_TOKEN, token)
                .httpOnly(true)
                .secure(fixSecure(secure, sameSite))
                .path("/")
                .sameSite(sameSite)
                .maxAge(maxAgeSeconds)
                .build().toString());
    }

    public static String getRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, REFRESH_TOKEN);
    }

    public static void clearRefreshToken(HttpServletResponse response, boolean secure, String sameSite) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(fixSecure(secure, sameSite))
                .path("/")
                .sameSite(sameSite)
                .maxAge(0)
                .build().toString());
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

    private static boolean fixSecure(boolean secure, String sameSite) {
        if (sameSite == null) {
            return secure;
        }

        return "None".equalsIgnoreCase(sameSite) || secure;
    }

    public static void consumeOAuthStateOrThrow(HttpServletRequest request, HttpServletResponse response, String state, boolean secure, String sameSite) {
        String stateCookie = getOAuthState(request);
        clearOAuthState(response, secure, sameSite);

        if (state == null || stateCookie == null || !state.equals(stateCookie)) {
            throw new UnauthorizedException(ErrorMessage.OAUTH_STATE_VALIDATION_FAILED);
        }
    }
}
