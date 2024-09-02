package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import validation.annotation.PasswordMatches;
import validation.annotation.UniqueLogin;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserRegistrationDTO implements UserDTO{
    @Size(min = 3, max = 100, message = "Username length must be between 3 and 100 characters")
    @NotBlank(message = "Username can not be empty")
    @UniqueLogin
    private String login;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Size(min = 6, max = 100, message = "Password length must be between 6 and 100 characters")
    @NotBlank(message = "Password can not be empty")
    private String password;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String confirmPassword;
}
