package util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static final String COOKIE_NAME = "SESSIONID";
    public static final int COOKIE_AGE = 60 * 60 * 24 * 7;

    private CookieUtils() {}

    public static Cookie createConfiguredCookie(String sessionId) {
        Cookie cookie = new Cookie(COOKIE_NAME, sessionId);
        cookie.setMaxAge(COOKIE_AGE);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }

    public static void deleteSessionCookie(HttpServletResponse response) {
        Cookie cookie = createConfiguredCookie("");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}