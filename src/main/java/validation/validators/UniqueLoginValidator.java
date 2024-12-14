package validation.validators;

import dao.UserRepository;
import entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Setter;
import validation.annotation.UniqueLogin;

import java.util.Optional;

public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {
    @Setter
    private static UserRepository userRepository;

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        Optional<User> user = userRepository.findUserByLogin(login);
        return user.isEmpty();
    }
}