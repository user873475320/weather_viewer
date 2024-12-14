package servlet.auth;

import dto.UserDTO;
import dto.UserLoginDTO;
import entity.Session;
import entity.User;
import exception.client.LoginException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import service.SessionService;
import service.UserService;
import servlet.BaseServlet;
import util.CookieUtils;
import util.ExceptionHandler;
import util.HttpSessionUtils;
import util.UserUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/auth/login")
public class LoginServlet extends BaseServlet {

    private UserService userService;
    private SessionService sessionService;

    @Override
    public void init(ServletConfig config) {
        exceptionHandler = (ExceptionHandler) config.getServletContext().getAttribute("exceptionHandler");
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        validator = (Validator) config.getServletContext().getAttribute("validator");

        userService = (UserService) config.getServletContext().getAttribute("userService");
        sessionService = (SessionService) config.getServletContext().getAttribute("sessionService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processTemplate("auth/login", req, resp, req.getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserLoginDTO userLoginDTO = UserUtils.getUserLoginDtoFromRequest(req);
        Set<ConstraintViolation<UserLoginDTO>> violations = validator.validate(userLoginDTO);

        if (!violations.isEmpty() || !userService.checkCredentials(userLoginDTO)) {
            throw new LoginException("Incorrect username or password");
        }

        handleWorkWithSessionAndCookie(req, resp, userLoginDTO);
        resp.sendRedirect("/home");
    }


    private void handleWorkWithSessionAndCookie(HttpServletRequest req, HttpServletResponse resp, UserDTO userDTO) {
        log.info("Start work with session and cookie");
        // Get User entity object from DB using UserDTO
        Optional<User> authorizedUser = userService.findUserByLoginAndPassword(userDTO.getLogin(), userDTO.getPassword());

        // Get configured session entity object
        Session session = sessionService.getConfiguredSession(authorizedUser.orElseThrow().getId());
        session.setUser(authorizedUser.orElseThrow());
        log.debug("Got configured session {} of the user with ID: {}", session, session.getUser().getId());

        // Save the session to DB
        sessionService.save(session);

        // Add our session and user objects to HttpSession
        HttpSessionUtils.createAndSetUpHttpSession(req, session);

        // Create cookie with sessionId from the previous session
        resp.addCookie(CookieUtils.createConfiguredCookie(session.getId()));
        log.debug("Added our session with UUID: {} into a cookie", session.getId());

        log.info("Finish work with session and cookie");
    }
}