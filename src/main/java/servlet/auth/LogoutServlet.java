package servlet.auth;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import service.SessionService;
import servlet.BaseServlet;
import util.CookieUtils;
import util.ExceptionHandler;
import util.HttpSessionUtils;

import java.io.IOException;

@Slf4j
@WebServlet("/auth/logout")
public class LogoutServlet extends BaseServlet {

    private SessionService sessionService;



    @Override
    public void init(ServletConfig config) {
        exceptionHandler = (ExceptionHandler) config.getServletContext().getAttribute("exceptionHandler");
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        validator = (Validator) config.getServletContext().getAttribute("validator");

        sessionService = (SessionService) config.getServletContext().getAttribute("sessionService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession httpSession = req.getSession();
        log.debug("Received http session from request: {}", httpSession);

        // Remove session from DB
        sessionService.delete(HttpSessionUtils.getSessionFromHttpSession(httpSession).getId());
        // Remove session from HttpSession
        httpSession.invalidate();
        log.debug("Invalidated our session");
        // Remove cookie with SESSIONID
        CookieUtils.deleteSessionCookie(resp);

        resp.sendRedirect("/index");
    }
}
