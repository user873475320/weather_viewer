package util;

import javax.servlet.http.Cookie;

public class CookieUtils {

    private CookieUtils() {
    }

    public static Cookie createConfiguredCookie(String sessionId) {
        // TODO: Maybe configure this cookie in another way
        Cookie cookie = new Cookie("SESSIONID", sessionId);
        cookie.setMaxAge(60*60*24*7);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}
