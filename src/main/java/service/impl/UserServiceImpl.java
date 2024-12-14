package service.impl;

import dao.UserRepository;
import dao.impl.UserRepositoryImpl;
import dto.UserDTO;
import entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = new UserRepositoryImpl();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        Optional<User> optionalUser = userRepository.findUserByLogin(login);
        return optionalUser
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    @Override
    public boolean checkCredentials(UserDTO userDTO) {
        return findUserByLoginAndPassword(userDTO.getLogin(), userDTO.getPassword()).isPresent();
    }

    @Override
    public void save(UserDTO userDTO) {
        userRepository.save(User.builder()
                .login(userDTO.getLogin())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build());
    }
}
