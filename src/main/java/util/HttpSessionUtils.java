package util;

import entity.Session;
import entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpSessionUtils {

    private HttpSessionUtils() {
    }

    public static Session getUserSessionFromHttpSession(HttpSession httpSession) {
        return (Session) httpSession.getAttribute("authorizedUserSession");
    }

    public static void createAndSetUpHttpSession(HttpServletRequest req, Session session) {
        HttpSession httpSession = req.getSession(true);
        httpSession.setAttribute("authorizedUser", session.getUser());
        httpSession.setAttribute("authorizedUserSession", session);
    }

    public static void createAndSetUpHttpSession(HttpServletRequest req, Session session, User user) {
        HttpSession httpSession = req.getSession(true);
        httpSession.setAttribute("authorizedUser", user);
        httpSession.setAttribute("authorizedUserSession", session);
    }

    public static void clearHttpSessionData(HttpSession httpSession) {
        httpSession.setAttribute("authorizedUserSession", null);
        httpSession.setAttribute("authorizedUser", null);
    }
}
