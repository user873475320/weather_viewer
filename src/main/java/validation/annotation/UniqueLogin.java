package validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import validation.validators.UniqueLoginValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = UniqueLoginValidator.class)
public @interface UniqueLogin {
    String message() default "Login is already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
