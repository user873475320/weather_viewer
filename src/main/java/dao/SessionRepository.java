package dao;

import entity.Session;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {

    Optional<Session> findSessionWithLoadedUserById(UUID id);

    void deleteExpiredSessions();

    void delete(UUID id);

    void save(Session session);
}
