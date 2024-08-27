package filter;

import configuration.ThymeleafConfig;
import entity.Session;
import exception.client.InvalidUserRequestException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import service.SessionService;
import util.CookieUtils;
import util.ExceptionHandler;
import util.HttpSessionUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private final List<String> unauthorizedUsersPaths = List.of("/index", "/auth/login", "/auth/registration");
    private final List<String> authorizedUsersPaths = List.of("/home", "/auth/logout", "/location");

    private final SessionService sessionService = new SessionService();
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        TemplateEngine templateEngine = new ThymeleafConfig(req.getServletContext()).getTemplateEngine();
        WebContext context = new WebContext(req, resp, req.getServletContext(), req.getLocale());

        try {
            String path = req.getServletPath();
            HttpSession httpSession = req.getSession(false);

            // We can find out about existence of valid session from httpSession or client cookies
            if (httpSession != null && HttpSessionUtils.getSessionFromHttpSession(httpSession) != null) {
                Session session = HttpSessionUtils.getSessionFromHttpSession(httpSession);

                if (sessionService.checkIfSessionIsValid(session)) {
                    actionsAfterSuccessfulAuthorization(path, req, resp, chain);
                } else {
                    // Clear data about user session from HttpSession and DB
                    httpSession.invalidate();
                    sessionService.delete(session.getId());
                    // Remove cookie with SESSIONID
                    CookieUtils.deleteSessionCookie(resp);

                    actionsAfterFailedAuthorization(path, req, resp, chain, templateEngine, context); // TODO: Add banner: "Your session expired. Login again"
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

                        actionsAfterFailedAuthorization(path, req, resp, chain, templateEngine, context); // TODO: Add banner: "Your session expired. Login again"
                    }
                } else {
                    actionsAfterFailedAuthorization(path, req, resp, chain, templateEngine, context); // TODO: Add banner: "Login before access this page"
                }
            }
        } catch (Exception e) {
            exceptionHandler.handle(e, resp, context, templateEngine);
        }
    }

    private void actionsAfterSuccessfulAuthorization(String path, HttpServletRequest req, HttpServletResponse resp,
                                                     FilterChain chain) throws IOException, ServletException {
        boolean isOnlyForUnauthorizedUsers = unauthorizedUsersPaths.contains(path);
        boolean isOnlyForAuthorizedUsers = authorizedUsersPaths.contains(path);
        boolean isRootPath = path.equals("/");

        if (isOnlyForUnauthorizedUsers || isRootPath) {
            resp.sendRedirect("/home");
        } else if (isOnlyForAuthorizedUsers) {
            chain.doFilter(req, resp);
        } else {
            throw new InvalidUserRequestException("Page not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void actionsAfterFailedAuthorization(String path, HttpServletRequest req, HttpServletResponse resp,
                                                 FilterChain chain, TemplateEngine templateEngine, WebContext context) throws ServletException, IOException {
        boolean isOnlyForUnauthorizedUsers = unauthorizedUsersPaths.contains(path);
        boolean isOnlyForAuthorizedUsers = authorizedUsersPaths.contains(path);
        boolean isRootPath = path.equals("/");

        if (isOnlyForUnauthorizedUsers) {
            chain.doFilter(req, resp);
        } else if (isRootPath) {
            templateEngine.process("index_not_authorized.html", context, resp.getWriter());
        } else if (isOnlyForAuthorizedUsers) {
            resp.sendRedirect("/index");
        } else {
            throw new InvalidUserRequestException("Page not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }
}