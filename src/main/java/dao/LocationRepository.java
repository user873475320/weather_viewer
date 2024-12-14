package dao;

import entity.Location;

import java.util.List;

public interface LocationRepository {

    void save(Location location);

    List<Location> findLocationsByUserId(Long userId);

    void delete(Location location);
}
