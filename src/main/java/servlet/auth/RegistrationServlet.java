package servlet.auth;

import dto.UserRegistrationDTO;
import exception.client.RegistrationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import service.UserService;
import servlet.BaseServlet;
import util.UserUtils;

import java.io.IOException;
import java.util.Set;

@WebServlet("/auth/registration")
public class RegistrationServlet extends BaseServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processTemplate("auth/registration", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserRegistrationDTO userRegistrationDTO = UserUtils.getUserRegistrationDtoFromRequest(req);
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(userRegistrationDTO);

        if (!violations.isEmpty()) {
            throw new RegistrationException(violations);
        }

        userService.save(userRegistrationDTO);
        resp.sendRedirect("/");
    }
}
