package servlet.auth;

import entity.Session;
import exception.ExceptionHandler;
import service.SessionService;
import util.CookieUtils;

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
            sessionService.delete(((Session) httpSession.getAttribute("authorizedUserSession")).getId());
            // Remove session from HttpSession
            httpSession.invalidate();
            // Remove cookie with SESSIONID
            CookieUtils.deleteSessionCookie(resp);

            resp.sendRedirect("/index");
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
}
