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

    private static final String SELECT_SESSION_BY_ID_SQL = "SELECT id, user_id, expires_at FROM sessions WHERE id = ?";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT id, login, password FROM users WHERE id = ?";
    private static final String DELETE_EXPIRED_SESSIONS_SQL = "DELETE FROM sessions WHERE expires_at < ?";
    private static final String DELETE_SESSION_BY_ID_SQL = "DELETE FROM sessions WHERE id = ?";
    private static final String INSERT_SESSION_SQL = "INSERT INTO sessions (id, user_id, expires_at) VALUES (?, ?, ?)";

    @Override
    public Optional<Session> findSessionWithLoadedUserById(UUID id) {
        try {
            Session session = jdbcTemplate.queryForObject(SELECT_SESSION_BY_ID_SQL, new SessionRowMapper(), id);

            if (session != null) {
                session.setUser(jdbcTemplate.queryForObject(SELECT_USER_BY_ID_SQL, new mapper.UserRowMapper(), session.getUser().getId()));
            }

            return Optional.ofNullable(session);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteExpiredSessions() {
        try {
            jdbcTemplate.update(DELETE_EXPIRED_SESSIONS_SQL, LocalDateTime.now());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            jdbcTemplate.update(DELETE_SESSION_BY_ID_SQL, id);
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public void save(Session session) {
        try {
            jdbcTemplate.update(INSERT_SESSION_SQL, session.getId(), session.getUser().getId(), session.getExpiresAt());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }
}
