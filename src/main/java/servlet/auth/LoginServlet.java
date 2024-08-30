package servlet.auth;

import dto.UserLoginDTO;
import exception.client.LoginException;
import service.UserService;
import servlet.BaseServlet;
import util.SessionHandler;
import util.UserUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Set;

@WebServlet("/auth/login")
public class LoginServlet extends BaseServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        templateEngine.process("auth/login", context, resp.getWriter());
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