package util;

import dto.UserLoginDTO;
import dto.UserRegistrationDTO;
import exception.client.InvalidUserRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserUtils {

    private UserUtils() {}

    public static UserLoginDTO getUserLoginDtoFromRequest(HttpServletRequest request) {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        if (login == null || password == null) {
            throw new InvalidUserRequestException("Some of the parameters are missing", HttpServletResponse.SC_BAD_REQUEST);
        }

        return new UserLoginDTO(login, password);
    }

    public static UserRegistrationDTO getUserRegistrationDtoFromRequest(HttpServletRequest request) {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");

        if (login == null || password == null || confirmPassword == null) {
            throw new InvalidUserRequestException("Some of the parameters are missing", HttpServletResponse.SC_BAD_REQUEST);
        }

        return new UserRegistrationDTO(login, password, confirmPassword);
    }
}
