package util;

import dto.UserLoginDTO;
import dto.UserRegistrationDTO;
import exception.client.InvalidUserRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserUtils {

    private UserUtils() {}

    public static UserLoginDTO getUserLoginDtoFromRequest(HttpServletRequest request) {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        if (login == null || password == null) {
            throw new InvalidUserRequestException("Some of the parameters are missing", HttpServletResponse.SC_BAD_REQUEST);
        }

        UserLoginDTO userLoginDTO = new UserLoginDTO(login, password);
        log.debug("Received UserLoginDTO: {}", userLoginDTO);
        return userLoginDTO;
    }

    public static UserRegistrationDTO getUserRegistrationDtoFromRequest(HttpServletRequest request) {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");

        if (login == null || password == null || confirmPassword == null) {
            throw new InvalidUserRequestException("Some of the parameters are missing", HttpServletResponse.SC_BAD_REQUEST);
        }

        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(login, password, confirmPassword);
        log.debug("Received UserRegistrationDTO: {}", userRegistrationDTO);
        return userRegistrationDTO;
    }
}
