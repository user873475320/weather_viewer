package service;

import dao.SessionDAO;
import entity.Session;
import entity.User;

import javax.servlet.http.Cookie;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

    public Session getSessionByIdAndLoadUser(String sessionId) {
        return sessionDAO.findByIdAndLoadUser(sessionId).orElse(null);
    }

    public void deleteSession(String sessionId) {
        sessionDAO.deleteById(sessionId);
    }

    public Session getValidSessionAndDeleteIfNot(@NotNull Cookie[] cookies) {
        List<Cookie> sessionCookies = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("SESSIONID")).toList();
        for (Cookie cookie : sessionCookies) {
            String sessionId = cookie.getValue();
            Session session = getSessionByIdAndLoadUser(sessionId);

            if (session != null) {
                if (LocalDateTime.now().isBefore(session.getExpiresAt())) {
                    return session;
                } else {
                    deleteSession(sessionId);
                }
            }
        }
        return null;
    }

    public boolean getValidSessionAndDeleteIfNot(@NotNull Session session) {
        if (LocalDateTime.now().isBefore(session.getExpiresAt())) {
            return true;
        } else {
            deleteSession(session.getId());
            return false;
        }
    }
}