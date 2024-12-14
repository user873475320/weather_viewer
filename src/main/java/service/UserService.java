package service;

import dto.UserDTO;
import entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByLoginAndPassword(String login, String password);

    boolean checkCredentials(UserDTO userDTO);

    void save(UserDTO userDTO);
}
