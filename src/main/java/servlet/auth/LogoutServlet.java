package servlet.auth;

import entity.Session;
import exception.ExceptionHandler;
import service.SessionService;
import util.CookieUtils;
import util.HttpSessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/auth/logout")
public class LogoutServlet extends HttpServlet {
    private final SessionService sessionService = new SessionService();
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            HttpSession httpSession = req.getSession();

            // Remove session from DB
            sessionService.deleteSession(((Session) httpSession.getAttribute("authorizedUserSession")).getId());
            // Remove session from HttpSession
            HttpSessionUtils.clearHttpSessionData(httpSession);
            // Remove cookie with SESSIONID
            CookieUtils.deleteSessionCookie(resp);

            resp.sendRedirect("/index");
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
}
