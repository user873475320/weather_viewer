package filter;

import entity.Session;
import service.SessionService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private final SessionService sessionService = new SessionService();

    public boolean checkValidSessionAndDeleteIfNot(Cookie[] cookies) {
        List<Cookie> sessionCookies = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("SESSIONID")).toList();
        for (Cookie cookie : sessionCookies) {
            String sessionId = cookie.getValue();
            Session session = sessionService.getSessionById(sessionId);

            if (session != null) {
                if (LocalDateTime.now().isBefore(session.getExpiresAt())) {
                    return true;
                } else {
                    sessionService.deleteSession(sessionId);
                }
            }
        }
        return false;
    }

    public boolean checkValidSessionAndDeleteIfNot(Session session) {
        if (LocalDateTime.now().isBefore(session.getExpiresAt())) {
            return true;
        } else {
            sessionService.deleteSession(session.getId());
            return false;
        }
    }

    public boolean isAuthPath(String path) {
        return path.equals("/") || path.equals("/auth/login") || path.equals("/auth/registration");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        boolean isAuthPath = isAuthPath(path);
        HttpSession httpSession = req.getSession(false);

        if (httpSession == null) {
            if (checkValidSessionAndDeleteIfNot(req.getCookies())) {
                if (isAuthPath) {
                    resp.sendRedirect("/home");
                } else {
                    chain.doFilter(request, response);
                }
            } else {
                if (isAuthPath) {
                    chain.doFilter(request, response);
                } else {
                    resp.sendRedirect("/");
                }
            }
        } else {
            Session session = (Session) httpSession.getAttribute("authorizedUserSession");

            if (session != null && checkValidSessionAndDeleteIfNot(session)) {
                if (isAuthPath) {
                    resp.sendRedirect("/home");
                } else {
                    chain.doFilter(request, response);
                }
            } else {
                if (isAuthPath) {
                    chain.doFilter(request, response);
                } else {
                    resp.sendRedirect("/");
                }
            }
        }
    }
}