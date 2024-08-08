package filter;

import configuration.ThymeleafConfig;
import entity.Session;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import service.SessionService;
import util.ExceptionHandler;
import util.HttpSessionUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
            Session session;

            // We can find out about valid session existence from httpSession or client cookies
            if (httpSession != null) {
                session = (Session) httpSession.getAttribute("authorizedUserSession");

                if (session != null && sessionService.getValidSessionAndDeleteIfNot(session)) {
                    actionsAfterSuccessfulAuthorization(path, req, resp, chain);
                } else {
                    actionsAfterFailedAuthorization(path, req, resp, chain, templateEngine, context);
                }
            } else {
                Cookie[] cookies = req.getCookies();

                if (cookies != null && (session = sessionService.getValidSessionAndDeleteIfNot(cookies)) != null) {
                    // Create and set up http session in order to in the next requests don't touch DB and use just http session
                    HttpSessionUtils.createAndSetUpHttpSession(req, session);
                    actionsAfterSuccessfulAuthorization(path, req, resp, chain);
                } else {
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
        boolean isAuthPath = isOnlyForUnauthorizedUsers(path);
        boolean isOnlyForAuthorizedUsers = isOnlyForAuthorizedUsers(path);

        if (isAuthPath || path.equals("/")) {
            //TODO: Add banner like: "You should logout before get access to this page"
            resp.sendRedirect("/home");
        } else if (isOnlyForAuthorizedUsers) {
            chain.doFilter(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void actionsAfterFailedAuthorization(String path, HttpServletRequest req, HttpServletResponse resp,
                                                 FilterChain chain, TemplateEngine templateEngine, WebContext context) throws ServletException, IOException {
        boolean isAuthPath = isOnlyForUnauthorizedUsers(path);
        boolean isOnlyForAuthorizedUsers = isOnlyForAuthorizedUsers(path);
        if (isAuthPath) {
            chain.doFilter(req, resp);
        } else if (path.equals("/")) {
            templateEngine.process("index_not_authorized.html", context, resp.getWriter());
        } else if (isOnlyForAuthorizedUsers) {
            //TODO: Add banner like: "You should authorize before get access to this page"
            resp.sendRedirect("/index");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}