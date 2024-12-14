package dao.impl;

import dao.LocationRepository;
import entity.Location;
import exception.server.DatabaseInteractionException;
import mapper.LocationRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;

public class LocationRepositoryImpl implements LocationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final LocationRowMapper locationRowMapper;

    public LocationRepositoryImpl() {
        this.jdbcTemplate = new JdbcTemplate(getDataSource());
        locationRowMapper = new LocationRowMapper();
    }

    private DriverManagerDataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/weatherViewerDB");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }

    @Override
    public void save(Location location) {
        try {
            String sql = "INSERT INTO locations (name, state, user_id, latitude, longitude) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, location.getName(), location.getState(), location.getUser().getId(),
                    location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public List<Location> findLocationsByUserId(Long userId) {
        try {
            String sql = "SELECT id, name, state, user_id, latitude, longitude FROM locations WHERE user_id = ? ORDER BY user_id ASC";
            return jdbcTemplate.query(sql, locationRowMapper, userId);
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public void delete(Location location) {
        try {
            String sql = "DELETE FROM locations WHERE user_id = ? AND latitude = ? AND longitude = ?";
            jdbcTemplate.update(sql, location.getUser().getId(), location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }
}
