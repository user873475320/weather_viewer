package service;

import dao.SessionDAO;
import entity.Session;
import entity.User;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import util.CookieUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class SessionService {

    private final SessionDAO sessionDAO = new SessionDAO();

    public Session getConfiguredSession(Long userId) {
        return new Session(
                UUID.randomUUID(),
                User.builder().id(userId).build(),
                LocalDateTime.now().plusWeeks(1)
        );
    }

    public Optional<Session> findSessionWithLoadedUserByCookies(Cookie[] cookies) {
        if (cookies != null) {
            Optional<Cookie> sessionCookie = Arrays
                    .stream(cookies)
                    .filter(cookie -> cookie.getName().equals(CookieUtils.COOKIE_NAME))
                    .findAny();
            return sessionCookie.flatMap(cookie -> sessionDAO.findSessionWithLoadedUserById(UUID.fromString(cookie.getValue())));
        } else {
            return Optional.empty();
        }
    }

    public boolean checkIfSessionIsValid(Session session) {
        return LocalDateTime.now().isBefore(session.getExpiresAt());
    }

    public void save(Session session) {
        sessionDAO.save(session);
    }

    public void delete(UUID sessionId) {
        sessionDAO.delete(sessionId);
    }

    public void deleteExpiredSessions() {
        log.info("Expired sessions was deleted");
        sessionDAO.deleteExpiredSessions();
    }
}