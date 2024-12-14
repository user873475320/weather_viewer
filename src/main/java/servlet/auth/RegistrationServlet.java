package servlet.auth;

import dto.UserRegistrationDTO;
import exception.client.RegistrationException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.thymeleaf.TemplateEngine;
import service.UserService;
import servlet.BaseServlet;
import util.ExceptionHandler;
import util.UserUtils;

import java.io.IOException;
import java.util.Set;

@WebServlet("/auth/registration")
public class RegistrationServlet extends BaseServlet {

    private UserService userService;

    @Override
    public void init(ServletConfig config) {
        exceptionHandler = (ExceptionHandler) config.getServletContext().getAttribute("exceptionHandler");
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        validator = (Validator) config.getServletContext().getAttribute("validator");

        userService = (UserService) config.getServletContext().getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processTemplate("auth/registration", req, resp, req.getServletContext());
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
