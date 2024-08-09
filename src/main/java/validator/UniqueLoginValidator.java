package validator;

import dao.UserDAO;
import entity.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Optional<User> user = userDAO.findByLogin(value);
        return user.isEmpty();
    }
}