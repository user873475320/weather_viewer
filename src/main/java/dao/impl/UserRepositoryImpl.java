package dao.impl;

import dao.UserRepository;
import entity.User;
import exception.server.DatabaseInteractionException;
import lombok.RequiredArgsConstructor;
import mapper.UserRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_USER_BY_LOGIN_SQL = "SELECT id, login, password FROM users WHERE login = ?";
    private static final String INSERT_USER_SQL = "INSERT INTO users (login, password) VALUES (?, ?)";

    @Override
    public Optional<User> findUserByLogin(String login) {
        try {
            User user = jdbcTemplate.queryForObject(SELECT_USER_BY_LOGIN_SQL, new UserRowMapper(), login);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }

    @Override
    public void save(User user) {
        try {
            jdbcTemplate.update(INSERT_USER_SQL, user.getLogin(), user.getPassword());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }
}
