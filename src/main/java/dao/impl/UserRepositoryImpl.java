package dao.impl;

import dao.UserRepository;
import entity.User;
import exception.server.DatabaseInteractionException;
import mapper.UserRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl() {
        this.jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    public DriverManagerDataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/weatherViewerDB");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }

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
