package validation.validators;

import dto.UserRegistrationDTO;
import validation.annotation.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationDTO> {

    @Override
    public boolean isValid(UserRegistrationDTO user, ConstraintValidatorContext context) {
        boolean isValid = user.getPassword().equals(user.getConfirmPassword());

        if (!isValid) {
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("password")
                .addConstraintViolation();
        }

        return isValid;
    }
}
