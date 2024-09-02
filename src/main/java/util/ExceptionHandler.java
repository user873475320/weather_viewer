package util;

import exception.client.InvalidUserRequestException;
import exception.client.LoginException;
import exception.client.RegistrationException;
import exception.server.ServletInitializationException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.UncheckedIOException;

@Slf4j
public class ExceptionHandler {
    private final ServletContext servletContext;
    private final TemplateEngine templateEngine;

    public ExceptionHandler(ServletContext servletContext, TemplateEngine templateEngine) {
        this.servletContext = servletContext;
        this.templateEngine = templateEngine;
    }

    protected void processTemplate(String templateName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Processing template: {}", templateName);
        IWebExchange iWebExchange = JakartaServletWebApplication.buildApplication(servletContext).buildExchange(request, response);
        WebContext webContext = new WebContext(iWebExchange);
        templateEngine.process(templateName, webContext, response.getWriter());
    }

    public void handle(Exception e, HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Return 4xx error
            switch (e) {
                case LoginException exc -> {
                    log.warn("Handling LoginException: {}", exc.getMessage());
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    req.setAttribute("violations", exc.getMessage());
                    processTemplate("auth/login", req, resp);
                }
                case RegistrationException exc -> {
                    log.warn("Handling RegistrationException: {}", exc.getViolations());
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    req.setAttribute("violations", exc.getViolations());
                    processTemplate("auth/registration", req, resp);
                }
                case InvalidUserRequestException exc -> {
                    log.warn("Handling InvalidUserRequestException: {}", exc.getMessage());
                    resp.setStatus(exc.getStatusCode());
                    if (exc.isOnlyShowBanner()) {
                        resp.setHeader("Error-Message", e.getMessage());
                    } else {
                        req.setAttribute("errorMessage", e.getMessage());
                        processTemplate("error_page", req, resp);
                    }
                }
                // Return 5xx error
                case null, default -> {
                    log.error("Handling unexpected exception: {}", (e != null) ? e.getMessage() : "exception object is null");
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    req.setAttribute("errorMessage", "Sorry. Internal server error. Try later");
                    processTemplate("error_page", req, resp);
                }
            }
        } catch (IOException ex) {
            log.error("Error processing template during exception handling", ex);
            throw new UncheckedIOException(ex);
        }
    }

    public void handle(ServletInitializationException e) {
        log.error("ServletInitializationException caught", e);
        throw e;
    }
}