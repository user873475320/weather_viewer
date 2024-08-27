package dao;

import entity.Location;
import exception.server.DatabaseInteractionException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtils;

import java.util.List;

public class LocationDAO {
    private final SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

    public List<Location> findLocationsByUserId(Long userId) {
        try (org.hibernate.Session hibernateSession = sessionFactory.getCurrentSession()) {
            hibernateSession.beginTransaction();

            List<Location> locations = hibernateSession.createQuery("select l from Location l where l.user.id = :userId", Location.class)
                    .setParameter("userId", userId)
                    .getResultList();

            hibernateSession.getTransaction().commit();

            return locations;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    public void save(Location location) {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.persist(location);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                throw new DatabaseInteractionException(e);
            }
        }
    }

    public void delete(Location location) {
        try (org.hibernate.Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                Query<?> query = hibernateSession
                        .createQuery("delete from Location l where l.user.id = :userId and l.latitude = :latitude and l.longitude = :longitude")
                        .setParameter("userId", location.getUser().getId())
                        .setParameter("latitude", location.getLatitude())
                        .setParameter("longitude", location.getLongitude());

                query.executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null)
                    transaction.rollback();

                throw new DatabaseInteractionException(e);
            }
        }
    }
}