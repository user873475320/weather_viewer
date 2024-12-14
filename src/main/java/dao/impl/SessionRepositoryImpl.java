package dao.impl;

import dao.SessionRepository;
import entity.Session;
import exception.server.DatabaseInteractionException;
import lombok.RequiredArgsConstructor;
import mapper.SessionRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Session> findSessionWithLoadedUserById(UUID id) {
        try {
            String sql = "SELECT id, user_id, expires_at FROM sessions WHERE id = ?";
            Session session = jdbcTemplate.queryForObject(sql, new SessionRowMapper(), id);

            if (session != null) {
                String userSql = "SELECT id, login, password FROM users WHERE id = ?";
                session.setUser(jdbcTemplate.queryForObject(userSql, new mapper.UserRowMapper(), session.getUser().getId()));
            }

            return Optional.ofNullable(session);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteExpiredSessions() {
        try {
            String sql = "DELETE FROM sessions WHERE expires_at < ?";
            jdbcTemplate.update(sql, LocalDateTime.now());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            String sql = "DELETE FROM sessions WHERE id = ?";
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public void save(Session session) {
        try {
            String sql = "INSERT INTO sessions (id, user_id, expires_at) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, session.getId(), session.getUser().getId(), session.getExpiresAt());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }
}
