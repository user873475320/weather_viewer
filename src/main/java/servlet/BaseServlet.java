package servlet;

import configuration.ThymeleafConfig;
import exception.server.ServletInitializationException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import util.ExceptionHandler;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;

public abstract class BaseServlet extends HttpServlet {
    protected TemplateEngine templateEngine;
    protected WebContext context;
    protected Validator validator;
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Override
    public void init(ServletConfig config) {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()){
            templateEngine = new ThymeleafConfig(config.getServletContext()).getTemplateEngine();
            validator = validatorFactory.getValidator();
            super.init(config);
        } catch (Exception e) {
            exceptionHandler.handle(new ServletInitializationException("Failed to initialize a servlet", e));
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        context = new WebContext(req, resp, getServletContext());

        try {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            super.service(req, resp);
        } catch (Exception e) {
            exceptionHandler.handle(e, resp, context, templateEngine);
        }
    }
}