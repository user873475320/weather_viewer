package dao;

import entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import utils.HibernateUtils;

import java.util.Optional;

public class UserDAO {

    private final SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

    public Optional<User> findByLogin(String login) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Optional<User> user = session
                    .createQuery("from User where login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();

            session.getTransaction().commit();

            return user;
        } catch (HibernateException e) {
            e.printStackTrace();
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.persist(user);
                transaction.commit();
            } catch (HibernateException e) {
                if (transaction != null)
                    transaction.rollback();

                e.printStackTrace();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                e.printStackTrace();
            }
        }
    }


    public void deleteByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                findByLogin(login).ifPresent(session::delete);
                transaction.commit();
            } catch (HibernateException e) {
                if (transaction != null)
                    transaction.rollback();

                e.printStackTrace();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                e.printStackTrace();
            }
        }
    }
}
