package filter;

import configuration.ThymeleafConfig;
import entity.Session;
import exception.client.InvalidUserRequestException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import service.SessionService;
import util.CookieUtils;
import util.ExceptionHandler;
import util.HttpSessionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private final List<String> unauthorizedUsersPaths = List.of("/index", "/auth/login", "/auth/registration", "");
    private final List<String> authorizedUsersPaths = List.of("/home", "/auth/logout", "/location");

    private ExceptionHandler exceptionHandler;
    private final SessionService sessionService = new SessionService();

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext servletContext = filterConfig.getServletContext();
        TemplateEngine templateEngine = new ThymeleafConfig().templateEngine(servletContext);
        exceptionHandler = new ExceptionHandler(servletContext, templateEngine);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

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

                    actionsAfterFailedAuthorization(path, req, resp, chain); // TODO: Add banner: "Your session expired. Login again"
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

                        actionsAfterFailedAuthorization(path, req, resp, chain); // TODO: Add banner: "Your session expired. Login again"
                    }
                } else {
                    actionsAfterFailedAuthorization(path, req, resp, chain); // TODO: Add banner: "Login before access this page"
                }
            }
        } catch (Exception e) {
            exceptionHandler.handle(e, req, resp);
        }
    }

    private void actionsAfterSuccessfulAuthorization(String path, HttpServletRequest req, HttpServletResponse resp,
                                                     FilterChain chain) throws IOException, ServletException {
        boolean isOnlyForUnauthorizedUsers = unauthorizedUsersPaths.contains(path);
        boolean isOnlyForAuthorizedUsers = authorizedUsersPaths.contains(path);

        if (isOnlyForUnauthorizedUsers) {
            resp.sendRedirect("/home");
        } else if (isOnlyForAuthorizedUsers) {
            chain.doFilter(req, resp);
        } else {
            throw new InvalidUserRequestException("Page not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void actionsAfterFailedAuthorization(String path, HttpServletRequest req, HttpServletResponse resp,
                                                 FilterChain chain) throws IOException, ServletException {
        boolean isOnlyForUnauthorizedUsers = unauthorizedUsersPaths.contains(path);
        boolean isOnlyForAuthorizedUsers = authorizedUsersPaths.contains(path);

        if (isOnlyForUnauthorizedUsers) {
            chain.doFilter(req, resp);
        }  else if (isOnlyForAuthorizedUsers) {
            resp.sendRedirect("/index");
        } else {
            throw new InvalidUserRequestException("Page not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }
}