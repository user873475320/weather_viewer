package servlet.auth;

import dto.UserRegistrationDTO;
import exception.client.RegistrationException;
import service.UserService;
import servlet.BaseServlet;
import util.UserUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Set;

@WebServlet("/auth/registration")
public class RegistrationServlet extends BaseServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        templateEngine.process("auth/registration", context, resp.getWriter());
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
