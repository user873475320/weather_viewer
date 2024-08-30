package exception.client;

import dto.UserRegistrationDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Set;

@Getter
public class RegistrationException extends RuntimeException {
    private Set<ConstraintViolation<UserRegistrationDTO>> violations;

    public RegistrationException(Set<ConstraintViolation<UserRegistrationDTO>> violations) {
        this.violations = violations;
    }
}