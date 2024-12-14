package dao.impl;

import dao.LocationRepository;
import entity.Location;
import exception.server.DatabaseInteractionException;
import lombok.RequiredArgsConstructor;
import mapper.LocationRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final LocationRowMapper locationRowMapper;

    private static final String SQL_SAVE_LOCATION = "INSERT INTO locations (name, state, user_id, latitude, longitude) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_FIND_LOCATIONS_BY_USER_ID = "SELECT id, name, state, user_id, latitude, longitude FROM locations WHERE user_id = ? ORDER BY user_id ASC";
    private static final String SQL_DELETE_LOCATION = "DELETE FROM locations WHERE user_id = ? AND latitude = ? AND longitude = ?";

    @Override
    public void save(Location location) {
        try {
            jdbcTemplate.update(SQL_SAVE_LOCATION, location.getName(), location.getState(), location.getUser().getId(),
                    location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public List<Location> findLocationsByUserId(Long userId) {
        try {
            return jdbcTemplate.query(SQL_FIND_LOCATIONS_BY_USER_ID, locationRowMapper, userId);
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public void delete(Location location) {
        try {
            jdbcTemplate.update(SQL_DELETE_LOCATION, location.getUser().getId(), location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }
}
