package dao;

import entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findUserByLogin(String login);

    void save(User user);
}
