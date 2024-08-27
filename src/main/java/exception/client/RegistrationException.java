package exception.client;

import dto.UserRegistrationDTO;
import lombok.Getter;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Getter
public class RegistrationException extends RuntimeException {
    private Set<ConstraintViolation<UserRegistrationDTO>> violations;

    public RegistrationException(Set<ConstraintViolation<UserRegistrationDTO>> violations) {
        this.violations = violations;
    }
}