package service;

import dao.SessionDAO;
import entity.Session;
import entity.User;

import javax.servlet.http.Cookie;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class SessionService {

    private final SessionDAO sessionDAO = new SessionDAO();

    public Session getConfiguredSession(Long userId) {
        return new Session(
                UUID.randomUUID().toString(),
                User.builder().id(userId).build(),
                LocalDateTime.now().plusWeeks(1)
        );
    }

    public Optional<Session> findSessionWithLoadedUserByCookies(Cookie[] cookies) {
        if (cookies != null) {
            Optional<Cookie> sessionCookie = Arrays
                    .stream(cookies)
                    .filter(cookie -> cookie.getName().equals("SESSIONID"))
                    .findAny();
            return sessionCookie.flatMap(cookie -> findSessionWithLoadedUserById(cookie.getValue()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Session> findSessionWithLoadedUserById(String sessionId) {
        return sessionDAO.findByIdAndLoadUser(sessionId);
    }

    public boolean checkIfSessionIsValid(@NotNull Session session) {
        return LocalDateTime.now().isBefore(session.getExpiresAt());
    }

    public void saveSession(Session session) {
        sessionDAO.save(session);
    }

    public void deleteSession(String sessionId) {
        sessionDAO.deleteById(sessionId);
    }

    public void deleteExpiredSessions() {
        sessionDAO.deleteExpiredSessions();
    }
}