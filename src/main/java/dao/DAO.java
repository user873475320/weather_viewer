package dao;

import exception.server.DatabaseInteractionException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtils;

import java.util.function.Consumer;

public abstract class DAO<T> {
    protected final SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

    public void save(T object) {
        executeInTransaction(hibernateSession ->
                hibernateSession.persist(object));
    }

    protected void executeInTransaction(Consumer<Session> action) {
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            try {
                action.accept(hibernateSession);
                hibernateSession.getTransaction().commit();
            }
            catch(Exception e){
                if (transaction != null) {
                    transaction.rollback();
                }

                throw new DatabaseInteractionException(e);
            }
        }
    }
}