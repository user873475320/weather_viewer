package util;

import exception.client.InvalidUserRequestException;
import exception.client.LoginException;
import exception.client.RegistrationException;
import exception.server.ServletInitializationException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ExceptionHandler {
    public void handle(Exception e, HttpServletResponse resp, WebContext context, TemplateEngine templateEngine) {
        try {
            // Return 4xx errors
            if (e instanceof LoginException) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                context.setVariable("violations", e.getMessage());
                templateEngine.process("auth/login", context, resp.getWriter());
            }
            else if (e instanceof RegistrationException exc) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                context.setVariable("violations", exc.getViolations());
                templateEngine.process("auth/registration", context, resp.getWriter());
            }
            else if (e instanceof InvalidUserRequestException exc) {
                resp.setStatus(exc.getStatusCode());
                if (exc.isOnlyShowBanner()) {
                    resp.setHeader("Error-Message", e.getMessage());
                } else {
                    context.setVariable("errorMessage", e.getMessage());
                    templateEngine.process("error_page", context, resp.getWriter());
                }
            }
            // Return 5xx error
            else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                context.setVariable("errorMessage", "Sorry. Internal server error. Try later");
                templateEngine.process("error_page", context, resp.getWriter());
            }
        } catch (IOException ex) {
            ex.printStackTrace(); // TODO: Log this properly
            throw new UncheckedIOException(ex);
        }
    }

    public void handle(ServletInitializationException e) {
        e.printStackTrace(); // TODO: Log this properly
        throw e;
    }
}