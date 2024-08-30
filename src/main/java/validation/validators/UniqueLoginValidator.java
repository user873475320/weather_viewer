package validation.validators;

import dao.UserDAO;
import entity.User;
import validation.annotation.UniqueLogin;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        Optional<User> user = userDAO.findUserByLogin(login);
        return user.isEmpty();
    }
}