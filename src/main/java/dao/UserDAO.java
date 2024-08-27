package dao;

import entity.User;
import exception.server.DatabaseInteractionException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtils;

import java.util.Optional;

public class UserDAO {

    private final SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

    public Optional<User> findUserByLogin(String login) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Optional<User> user = session
                    .createQuery("from User where login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();

            session.getTransaction().commit();

            return user;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    public void save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.persist(user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                throw new DatabaseInteractionException(e);
            }
        }
    }
}
