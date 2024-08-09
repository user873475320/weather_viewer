package servlet.auth;

import configuration.ThymeleafConfig;
import dto.UserLoginDTO;
import entity.Session;
import entity.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import service.AuthenticationService;
import service.SessionService;
import util.CookieUtils;
import util.ExceptionHandler;
import util.HttpSessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {

    private Validator validator;
    private final AuthenticationService authenticationService = new AuthenticationService();
    private final SessionService sessionService = new SessionService();
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

            String login = req.getParameter("login").strip();
            String password = req.getParameter("password").strip();
            UserLoginDTO userLoginDTO = new UserLoginDTO(login, password);

            Set<ConstraintViolation<UserLoginDTO>> violations = validator.validate(userLoginDTO);
            Optional<User> authorizedUser = authenticationService.findUserByLoginAndPassword(login, password);

            if (violations.isEmpty() && authorizedUser.isPresent()) {
                Session session = sessionService.getConfiguredSession(authorizedUser.get().getId());
                sessionService.saveSession(session);

                HttpSessionUtils.createAndSetUpHttpSession(req, session, authorizedUser.get());

                resp.addCookie(CookieUtils.createConfiguredCookie(session.getId()));
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.sendRedirect("/home");
            } else {
                // TODO: Maybe change status code when we have some errors in fields
                context.setVariable("violations", "Incorrect username or password");
                templateEngine.process("auth/login", context, resp.getWriter());
            }
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
}
