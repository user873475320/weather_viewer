package dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import validation.annotation.PasswordMatches;
import validation.annotation.UniqueLogin;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserRegistrationDTO implements UserDTO{
    @Size(min = 3, max = 100, message = "Username length must be between 3 and 100 characters")
    @NotBlank(message = "Username can not be empty")
    @UniqueLogin
    private String login;

    @Size(min = 6, max = 100, message = "Password length must be between 6 and 100 characters")
    @NotBlank(message = "Password can not be empty")
    private String password;

    private String confirmPassword;
}
