package service;

import entity.Session;
import jakarta.servlet.http.Cookie;

import java.util.Optional;
import java.util.UUID;

public interface SessionService {

    Session getConfiguredSession(Long userId);

    Optional<Session> findSessionWithLoadedUserByCookies(Cookie[] cookies);

    boolean checkIfSessionIsValid(Session session);

    void save(Session session);

    void delete(UUID sessionId);

    void deleteExpiredSessions();
}
