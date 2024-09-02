package util;

import entity.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpSessionUtils {

    public static final String USER_ATTRIBUTE_NAME_IN_HTTP_SESSION = "authorizedUser";
    public static final String SESSION_ATTRIBUTE_NAME_IN_HTTP_SESSION = "authorizedUserSession";

    private HttpSessionUtils() {}

    public static Session getSessionFromHttpSession(@NotNull HttpSession httpSession) {
        Session session = (Session) httpSession.getAttribute(SESSION_ATTRIBUTE_NAME_IN_HTTP_SESSION);
        log.debug("Retrieved session from http session: {}", session);
        return session;
    }

    public static void createAndSetUpHttpSession(HttpServletRequest req, Session session) {
        HttpSession httpSession = req.getSession(true);
        httpSession.setAttribute(USER_ATTRIBUTE_NAME_IN_HTTP_SESSION, session.getUser());
        httpSession.setAttribute(SESSION_ATTRIBUTE_NAME_IN_HTTP_SESSION, session);
        log.debug("Create session for user ID: {}", session.getUser().getId());
    }
}
