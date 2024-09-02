package service;

import dao.UserDAO;
import dto.UserDTO;
import entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        Optional<User> optionalUser = userDAO.findUserByLogin(login);
        return optionalUser
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public boolean checkCredentials(UserDTO userDTO) {
        return findUserByLoginAndPassword(userDTO.getLogin(), userDTO.getPassword()).isPresent();
    }

    public void save(UserDTO userDTO) {
        userDAO.save(User.builder()
                .login(userDTO.getLogin())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build());
    }
}
