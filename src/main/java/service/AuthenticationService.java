package service;

import dao.UserDAO;
import entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


public class AuthenticationService {

    private final UserDAO userDAO = new UserDAO();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getUserByLoginAndPassword(String login, String password) {
        Optional<User> optionalUser = userDAO.findByLogin(login);
        return optionalUser
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }

    public void addUser(String login, String password) {
        userDAO.save(User.builder()
                .login(login)
                .password(passwordEncoder.encode(password))
                .build());
    }
}
