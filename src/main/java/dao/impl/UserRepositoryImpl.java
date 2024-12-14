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

    @Override
    public Optional<User> findUserByLogin(String login) {
        try {
            String sql = "SELECT id, login, password FROM users WHERE login = ?";
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), login);
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
            String sql = "INSERT INTO users (login, password) VALUES (?, ?)";
            jdbcTemplate.update(sql, user.getLogin(), user.getPassword());
        } catch (Exception e) {
            throw new DatabaseInteractionException(e);
        }
    }
}
