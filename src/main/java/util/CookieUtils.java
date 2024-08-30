package util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class CookieUtils {

    public static final String COOKIE_NAME = "SESSIONID";
    public static final int COOKIE_AGE = 60 * 60 * 24 * 7;

    private CookieUtils() {}

    public static Cookie createConfiguredCookie(UUID sessionId) {
        Cookie cookie = new Cookie(COOKIE_NAME, sessionId.toString());
        cookie.setMaxAge(COOKIE_AGE);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }

    public static void deleteSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}