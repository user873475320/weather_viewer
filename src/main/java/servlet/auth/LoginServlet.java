package servlet.auth;

import dto.UserLoginDTO;
import exception.client.LoginException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import service.UserService;
import servlet.BaseServlet;
import util.SessionHandler;
import util.UserUtils;

import java.io.IOException;
import java.util.Set;

@WebServlet("/auth/login")
public class LoginServlet extends BaseServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processTemplate("auth/login", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserLoginDTO userLoginDTO = UserUtils.getUserLoginDtoFromRequest(req);
        Set<ConstraintViolation<UserLoginDTO>> violations = validator.validate(userLoginDTO);

        if (!violations.isEmpty() || !userService.checkCredentials(userLoginDTO)) {
            throw new LoginException("Incorrect username or password");
        }

        SessionHandler.handleWorkWithSessionAndCookie(req, resp, userLoginDTO);
        resp.sendRedirect("/home");
    }
}