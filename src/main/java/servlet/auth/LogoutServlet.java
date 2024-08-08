package servlet.auth;

import entity.Session;
import service.SessionService;
import util.ExceptionHandler;
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

            sessionService.deleteSession(((Session) httpSession.getAttribute("authorizedUserSession")).getId());
            HttpSessionUtils.clearHttpSessionData(httpSession);

            resp.sendRedirect("/index");
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
}
