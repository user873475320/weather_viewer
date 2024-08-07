package dao;


import exception.DatabaseInteractionException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtils;

import javax.transaction.Transactional;
import java.util.Optional;

public class SessionDAO {

    private final SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

    public Optional<entity.Session> findById(String id) {
        try (Session hibernateSession = sessionFactory.getCurrentSession()) {
            hibernateSession.beginTransaction();

            Optional<entity.Session> session = Optional.ofNullable(hibernateSession.get(entity.Session.class, id));

            hibernateSession.getTransaction().commit();

            return session;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    // TODO: Remove this method(or make user EAGER in session entity) and adding user object into session in general
    public Optional<entity.Session> findByIdAndLoadUser(String id) {
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
    public void save(entity.Session session) {
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

    public void deleteById(String id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                findById(id).ifPresent(session::delete);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                throw new DatabaseInteractionException(e);
            }
        }
    }
}
