package dao;

import dao.mapper.SessionRowMapper;
import entity.Session;
import exception.server.DatabaseInteractionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SessionDAO {

    private final JdbcTemplate jdbcTemplate;

    public SessionDAO() {
        this.jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    private DriverManagerDataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/weatherViewerDB");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }

    public Optional<Session> findSessionWithLoadedUserById(UUID id) {
        try {
            String sql = "SELECT id, user_id, expires_at FROM sessions WHERE id = ?";
            Session session = jdbcTemplate.queryForObject(sql, new SessionRowMapper(), id);

            if (session != null) {
                String userSql = "SELECT id, login, password FROM users WHERE id = ?";
                session.setUser(jdbcTemplate.queryForObject(userSql, new dao.mapper.UserRowMapper(), session.getUser().getId()));
            }

            return Optional.ofNullable(session);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteExpiredSessions() {
        try {
            String sql = "DELETE FROM sessions WHERE expires_at < ?";
            jdbcTemplate.update(sql, LocalDateTime.now());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    public void delete(UUID id) {
        try {
            String sql = "DELETE FROM sessions WHERE id = ?";
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    public void save(Session session) {
        try {
            String sql = "INSERT INTO sessions (id, user_id, expires_at) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, session.getId(), session.getUser().getId(), session.getExpiresAt());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }
}
