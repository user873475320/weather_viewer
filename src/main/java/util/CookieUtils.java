package util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    private CookieUtils() {}

    public static Cookie createConfiguredCookie(String sessionId) {
        Cookie cookie = new Cookie("SESSIONID", sessionId);
        cookie.setMaxAge(60*60*24*7);
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