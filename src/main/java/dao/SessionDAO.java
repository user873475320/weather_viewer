package dao;

import exception.server.DatabaseInteractionException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SessionDAO extends DAO<entity.Session> {

    public Optional<entity.Session> findSessionById(UUID id) {
        try (Session hibernateSession = sessionFactory.openSession()) {
            hibernateSession.beginTransaction();

            Optional<entity.Session> session = Optional.ofNullable(hibernateSession.get(entity.Session.class, id));

            hibernateSession.getTransaction().commit();

            return session;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    public Optional<entity.Session> findSessionWithLoadedUserById(UUID id) {
        try (Session hibernateSession = sessionFactory.openSession()) {
            hibernateSession.beginTransaction();

            Optional<entity.Session> session = Optional.ofNullable(hibernateSession.get(entity.Session.class, id));
            session.ifPresent(value -> Hibernate.initialize(value.getUser()));

            hibernateSession.getTransaction().commit();

            return session;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    public void deleteExpiredSessions() {
        executeInTransaction(hibernateSession -> {
            Query<?> query = hibernateSession.createQuery("delete from Session s where s.expiresAt < :now");
            query.setParameter("now", LocalDateTime.now());
            query.executeUpdate();
        });
    }

    public void delete(UUID id) {
        executeInTransaction(hibernateSession ->
                findSessionById(id).ifPresent(hibernateSession::delete));
    }
}
