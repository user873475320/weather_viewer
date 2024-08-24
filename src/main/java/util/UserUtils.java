package util;

import dto.UserLoginDTO;
import dto.UserRegistrationDTO;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {

    private UserUtils() {}

    public static UserLoginDTO getUserLoginDtoFromRequest(HttpServletRequest request) {
        String login = request.getParameter("login").strip();
        String password = request.getParameter("password").strip();
        return new UserLoginDTO(login, password);
    }

    public static UserRegistrationDTO getUserRegistrationDtoFromRequest(HttpServletRequest request) {
        String login = request.getParameter("login").strip();
        String password = request.getParameter("password").strip();
        String confirmPassword = request.getParameter("confirm-password").strip();

        return new UserRegistrationDTO(login, password, confirmPassword);
    }
}
