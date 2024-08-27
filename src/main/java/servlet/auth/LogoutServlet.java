package servlet.auth;

import service.SessionService;
import servlet.BaseServlet;
import util.CookieUtils;
import util.HttpSessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth/logout")
public class LogoutServlet extends BaseServlet {
    private final SessionService sessionService = new SessionService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession httpSession = req.getSession();

        // Remove session from DB
        sessionService.delete(HttpSessionUtils.getSessionFromHttpSession(httpSession).getId());
        // Remove session from HttpSession
        httpSession.invalidate();
        // Remove cookie with SESSIONID
        CookieUtils.deleteSessionCookie(resp);

        resp.sendRedirect("/index");
    }
}
