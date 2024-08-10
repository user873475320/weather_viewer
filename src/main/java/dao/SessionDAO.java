package dao;

import exception.DatabaseInteractionException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public class SessionDAO {

    private final SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

    public Optional<entity.Session> findSessionById(String id) {
        try (Session hibernateSession = sessionFactory.getCurrentSession()) {
            hibernateSession.beginTransaction();

            Optional<entity.Session> session = Optional.ofNullable(hibernateSession.get(entity.Session.class, id));

            hibernateSession.getTransaction().commit();

            return session;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    public Optional<entity.Session> findSessionWithLoadedUserById(String id) {
        try (Session hibernateSession = sessionFactory.getCurrentSession()) {
            hibernateSession.beginTransaction();

            Optional<entity.Session> session = Optional.ofNullable(hibernateSession.get(entity.Session.class, id));
            session.ifPresent(value -> Hibernate.initialize(value.getUser()));

            hibernateSession.getTransaction().commit();

            return session;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Transactional
    public void saveSession(entity.Session session) {
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                hibernateSession.persist(session);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                throw new DatabaseInteractionException(e);
            }
        }
    }

    public void deleteExpiredSessions() {
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                Query<?> query = hibernateSession.createQuery("delete from Session s where s.expiresAt < :now");
                query.setParameter("now", LocalDateTime.now());

                query.executeUpdate();

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                throw new DatabaseInteractionException(e);
            }
        }
    }

    public void deleteById(String id) {
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                findSessionById(id).ifPresent(hibernateSession::delete);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                throw new DatabaseInteractionException(e);
            }
        }
    }
}
