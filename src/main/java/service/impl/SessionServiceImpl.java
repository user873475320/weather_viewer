package service.impl;

import dao.SessionRepository;
import entity.Session;
import entity.User;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.SessionService;
import util.CookieUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public Session getConfiguredSession(Long userId) {
        return new Session(
                UUID.randomUUID(),
                User.builder().id(userId).build(),
                LocalDateTime.now().plusWeeks(1)
        );
    }

    @Override
    public Optional<Session> findSessionWithLoadedUserByCookies(Cookie[] cookies) {
        if (cookies != null) {
            Optional<Cookie> sessionCookie = Arrays
                    .stream(cookies)
                    .filter(cookie -> cookie.getName().equals(CookieUtils.COOKIE_NAME))
                    .findAny();
            return sessionCookie.flatMap(cookie -> sessionRepository.findSessionWithLoadedUserById(UUID.fromString(cookie.getValue())));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean checkIfSessionIsValid(Session session) {
        return LocalDateTime.now().isBefore(session.getExpiresAt());
    }

    @Override
    public void save(Session session) {
        sessionRepository.save(session);
    }

    @Override
    public void delete(UUID sessionId) {
        sessionRepository.delete(sessionId);
    }

    @Override
    public void deleteExpiredSessions() {
        log.info("Expired sessions was deleted");
        sessionRepository.deleteExpiredSessions();
    }
}