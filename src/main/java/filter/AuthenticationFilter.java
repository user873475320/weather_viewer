package filter;

import configuration.ThymeleafConfig;
import entity.Session;
import exception.ExceptionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import service.SessionService;
import util.CookieUtils;
import util.HttpSessionUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private final SessionService sessionService = new SessionService();
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        try {
            TemplateEngine templateEngine = new ThymeleafConfig(req.getServletContext()).getTemplateEngine();
            WebContext context = new WebContext(req, resp, req.getServletContext(), req.getLocale());

            String path = req.getServletPath();
            HttpSession httpSession = req.getSession(false);

            // We can find out about existence of valid session from httpSession or client cookies
            if (httpSession != null && HttpSessionUtils.getSessionFromHttpSession(httpSession) != null) {
                Session session = HttpSessionUtils.getSessionFromHttpSession(httpSession);

                if (sessionService.checkIfSessionIsValid(session)) {
                    actionsAfterSuccessfulAuthorization(path, req, resp, chain);
                } else {
                    // Clear data about user session from HttpSession and DB
                    HttpSessionUtils.clearHttpSessionData(httpSession);
                    sessionService.deleteSession(session.getId());
                    sessionService.delete(session.getId());
                    // Remove cookie with SESSIONID
                    CookieUtils.deleteSessionCookie(resp);

                    // TODO: Add banner: "Your session expired. Login again"
                    actionsAfterFailedAuthorization(path, req, resp, chain, templateEngine, context);
                }
            } else {
                Cookie[] cookies = req.getCookies();
                Optional<Session> session = sessionService.findSessionWithLoadedUserByCookies(cookies);

                if (session.isPresent()) {
                    if (sessionService.checkIfSessionIsValid(session.get())) {
                        // Create and set up http session in order to in the next requests don't touch DB and use just http session
                        HttpSessionUtils.createAndSetUpHttpSession(req, session.get());
                        actionsAfterSuccessfulAuthorization(path, req, resp, chain);
                    } else {
                        // Clear data about user session from DB
                        sessionService.delete(session.get().getId());
                        // Remove cookie with SESSIONID
                        CookieUtils.deleteSessionCookie(resp);

                        // TODO: Add banner: "Your session expired. Login again"
                        actionsAfterFailedAuthorization(path, req, resp, chain, templateEngine, context);
                    }
                } else {
                    // TODO: Add banner: "Login before access this page"
                    actionsAfterFailedAuthorization(path, req, resp, chain, templateEngine, context);
                }
            }
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    private boolean isOnlyForUnauthorizedUsers(String path) {
        return path.equals("/index") || path.equals("/auth/login") || path.equals("/auth/registration");
    }

    private boolean isOnlyForAuthorizedUsers(String path) {
        return path.equals("/home") || path.equals("/auth/logout");
    }

    private void actionsAfterSuccessfulAuthorization(String path, HttpServletRequest req, HttpServletResponse resp,
                                                     FilterChain chain) throws IOException, ServletException {
        boolean isOnlyForUnauthorizedUsers = isOnlyForUnauthorizedUsers(path);
        boolean isOnlyForAuthorizedUsers = isOnlyForAuthorizedUsers(path);
        boolean isRootPath = path.equals("/");

        if (isOnlyForUnauthorizedUsers || isRootPath) {
            resp.sendRedirect("/home");
        } else if (isOnlyForAuthorizedUsers) {
            chain.doFilter(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void actionsAfterFailedAuthorization(String path, HttpServletRequest req, HttpServletResponse resp,
                                                 FilterChain chain, TemplateEngine templateEngine, WebContext context) throws ServletException, IOException {
        boolean isOnlyForUnauthorizedUsers = isOnlyForUnauthorizedUsers(path);
        boolean isOnlyForAuthorizedUsers = isOnlyForAuthorizedUsers(path);
        boolean isRootPath = path.equals("/");

        if (isOnlyForUnauthorizedUsers) {
            chain.doFilter(req, resp);
        } else if (isRootPath) {
            templateEngine.process("index_not_authorized.html", context, resp.getWriter());
        } else if (isOnlyForAuthorizedUsers) {
            resp.sendRedirect("/index");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}