package dao;

import entity.Location;
import exception.server.DatabaseInteractionException;

import java.util.List;

public class LocationDAO extends DAO<Location> {

    public List<Location> findLocationsByUserId(Long userId) {
        try (org.hibernate.Session hibernateSession = sessionFactory.getCurrentSession()) {
            hibernateSession.beginTransaction();

            List<Location> locations = hibernateSession.createQuery("select l from Location l where l.user.id = :userId ORDER BY l.user.id ASC", Location.class)
                    .setParameter("userId", userId)
                    .getResultList();

            hibernateSession.getTransaction().commit();

            return locations;
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    public void delete(Location location) {
        executeInTransaction(hibernateSession -> hibernateSession
                .createQuery("delete from Location l where l.user.id = :userId and l.latitude = :latitude and l.longitude = :longitude")
                .setParameter("userId", location.getUser().getId())
                .setParameter("latitude", location.getLatitude())
                .setParameter("longitude", location.getLongitude())
                .executeUpdate()
        );
    }
}