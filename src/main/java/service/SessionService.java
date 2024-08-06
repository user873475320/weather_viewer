package service;

import dao.SessionDAO;
import entity.Session;
import entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionService {

    private final SessionDAO sessionDAO = new SessionDAO();

    public Session createAndSaveSession(Long userId) {
        Session session = new Session(
                UUID.randomUUID().toString(),
                User.builder().id(userId).build(),
                LocalDateTime.now().plusWeeks(1)
        );

        sessionDAO.save(session);
        return session;
    }

    public Session getSessionById(String sessionId) {
        return sessionDAO.findById(sessionId).orElse(null);
    }

    public void deleteSession(String sessionId) {
        sessionDAO.deleteById(sessionId);
    }
}