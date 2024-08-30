package servlet;

import configuration.ThymeleafConfig;
import exception.server.ServletInitializationException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import util.ExceptionHandler;

import java.io.IOException;

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