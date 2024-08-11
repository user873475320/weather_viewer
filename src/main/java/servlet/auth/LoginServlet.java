package servlet.auth;

import configuration.ThymeleafConfig;
import dto.UserLoginDTO;
import exception.ExceptionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import service.AuthenticationService;
import util.SessionHandler;
import util.UserUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {

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
            templateEngine.process("auth/login", context, resp.getWriter());
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateEngine templateEngine = new ThymeleafConfig(getServletContext()).getTemplateEngine();
            WebContext context = new WebContext(req, resp, getServletContext(), req.getLocale());

            UserLoginDTO userLoginDTO = UserUtils.getUserLoginDtoFromRequest(req);

            Set<ConstraintViolation<UserLoginDTO>> violations = validator.validate(userLoginDTO);

            if (violations.isEmpty() && authenticationService.checkCredentials(userLoginDTO)) {
                SessionHandler.handleWorkWithSessionAndCookie(req, resp, userLoginDTO);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.sendRedirect("/home");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                context.setVariable("violations", "Incorrect username or password");
                templateEngine.process("auth/login", context, resp.getWriter());
            }
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
}