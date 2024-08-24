package util;

import entity.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

public class HttpSessionUtils {

    private HttpSessionUtils() {}

    public static Session getSessionFromHttpSession(@NotNull HttpSession httpSession) {
        return (Session) httpSession.getAttribute("authorizedUserSession");
    }

    public static void createAndSetUpHttpSession(HttpServletRequest req, Session session) {
        HttpSession httpSession = req.getSession(true);
        httpSession.setAttribute("authorizedUser", session.getUser());
        httpSession.setAttribute("authorizedUserSession", session);
    }
}
