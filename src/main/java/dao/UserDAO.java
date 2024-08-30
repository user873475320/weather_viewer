package dao;

import entity.User;
import exception.server.DatabaseInteractionException;
import org.hibernate.Session;

import java.util.Optional;

public class UserDAO extends DAO<User> {

    public Optional<User> findUserByLogin(String login) {
        try (Session hibernateSession = sessionFactory.getCurrentSession()) {
            hibernateSession.beginTransaction();

            Optional<User> user = hibernateSession
                    .createQuery("from User where login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();

            hibernateSession.getTransaction().commit();

            return user;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }
}
