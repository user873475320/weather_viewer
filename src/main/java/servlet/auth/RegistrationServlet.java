package servlet.auth;

import configuration.ThymeleafConfig;
import dto.UserDTO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import service.AuthenticationService;
import util.ExceptionHandler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@WebServlet("/auth/registration")
public class RegistrationServlet extends HttpServlet {

    private Validator validator;
    private final AuthenticationService authenticationService = new AuthenticationService();
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Override
    public void init() {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateEngine templateEngine = new ThymeleafConfig(getServletContext()).getTemplateEngine();
            WebContext context = new WebContext(req, resp, getServletContext(), req.getLocale());
            templateEngine.process("auth/registration", context, resp.getWriter());
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateEngine templateEngine = new ThymeleafConfig(getServletContext()).getTemplateEngine();
            WebContext context = new WebContext(req, resp, getServletContext(), req.getLocale());

            // TODO: Create method for extracting userDTO from request
            String login = req.getParameter("login").strip();
            String password = req.getParameter("password").strip();
            String confirmPassword = req.getParameter("confirm-password").strip();

            UserDTO userDTO = new UserDTO(login, password, confirmPassword);

            Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);

            if (violations.isEmpty()) {
                authenticationService.addUser(login, password);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                // TODO: Redirect to index page with adding a successful banner
                resp.sendRedirect("/");
            } else {
                // TODO: Maybe change status code when we have any errors in fields
                context.setVariable("violations", violations);
                templateEngine.process("auth/registration", context, resp.getWriter());
            }
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
}
