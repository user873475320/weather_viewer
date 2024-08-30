package servlet.auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.SessionService;
import servlet.BaseServlet;
import util.CookieUtils;
import util.HttpSessionUtils;

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
