package servlet.auth;

import configuration.ThymeleafConfig;
import dto.UserRegistrationDTO;
import exception.ExceptionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import service.AuthenticationService;
import util.UserUtils;

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

            resp.setContentType("text/html; charset=UTF-8");

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

            UserRegistrationDTO userRegistrationDTO = UserUtils.getUserRegistrationDtoFromRequest(req);

            Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(userRegistrationDTO);

            if (violations.isEmpty()) {
                authenticationService.saveUser(userRegistrationDTO);

                // TODO: Add banner: "You successfully registered!"
                resp.sendRedirect("/");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("text/html; charset=UTF-8");
                context.setVariable("violations", violations);
                templateEngine.process("auth/registration", context, resp.getWriter());
            }
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
}
