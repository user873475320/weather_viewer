package util;

import entity.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

public class HttpSessionUtils {

    public static final String USER_ATTRIBUTE_NAME_IN_HTTP_SESSION = "authorizedUser";
    public static final String SESSION_ATTRIBUTE_NAME_IN_HTTP_SESSION = "authorizedUserSession";

    private HttpSessionUtils() {}

    public static Session getSessionFromHttpSession(@NotNull HttpSession httpSession) {
        return (Session) httpSession.getAttribute(SESSION_ATTRIBUTE_NAME_IN_HTTP_SESSION);
    }

    public static void createAndSetUpHttpSession(HttpServletRequest req, Session session) {
        HttpSession httpSession = req.getSession(true);
        httpSession.setAttribute(USER_ATTRIBUTE_NAME_IN_HTTP_SESSION, session.getUser());
        httpSession.setAttribute(SESSION_ATTRIBUTE_NAME_IN_HTTP_SESSION, session);
    }
}
