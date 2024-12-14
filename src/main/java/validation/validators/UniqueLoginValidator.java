package validation.validators;

import dao.UserRepository;
import dao.impl.UserRepositoryImpl;
import entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import validation.annotation.UniqueLogin;

import java.util.Optional;

public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {
    private final UserRepository userRepository = new UserRepositoryImpl();

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        Optional<User> user = userRepository.findUserByLogin(login);
        return user.isEmpty();
    }
}