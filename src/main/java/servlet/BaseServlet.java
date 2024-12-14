package servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import util.ExceptionHandler;

import java.io.IOException;

@Slf4j
public abstract class BaseServlet extends HttpServlet {
    protected TemplateEngine templateEngine;
    protected Validator validator;
    protected ExceptionHandler exceptionHandler;

    protected void processTemplate(String templateName, HttpServletRequest request, HttpServletResponse response, ServletContext context) throws IOException {
        log.info("Processing template: {}", templateName);
        IWebExchange iWebExchange = JakartaServletWebApplication.buildApplication(context).buildExchange(request, response);
        WebContext webContext = new WebContext(iWebExchange);
        templateEngine.process(templateName, webContext, response.getWriter());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        log.info("Processing {} request for {}", req.getMethod(), req.getServletPath());
        try {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");

            super.service(req, resp);
            log.info("Successfully processed {} request for {}", req.getMethod(), req.getServletPath());
        } catch (Exception e) {
            exceptionHandler.handle(e, req, resp);
        }
    }
}