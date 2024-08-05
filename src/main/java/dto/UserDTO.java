package dto;


import lombok.*;
import validator.PasswordMatches;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserDTO {
    @Size(min = 3, max = 100, message = "Username length must be between 3 and 100 characters")
    @NotBlank(message = "Username can not be empty")
    private String login;

    @Size(min = 6, max = 100, message = "Password length must be between 6 and 100 characters")
    @NotBlank(message = "Password can not be empty")
    private String password;

    private String confirmPassword;

    public UserDTO(String login, String password) {
        this.login = login;
        this.password = password;
        this.confirmPassword = password;
    }
}
