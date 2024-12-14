package servlet.auth;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import org.thymeleaf.TemplateEngine;
import servlet.BaseServlet;
import util.ExceptionHandler;

import java.io.IOException;

@WebServlet(urlPatterns = {"/index", ""})
public class IndexServlet extends BaseServlet {

    @Override
    public void init(ServletConfig config) {
        exceptionHandler = (ExceptionHandler) config.getServletContext().getAttribute("exceptionHandler");
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        validator = (Validator) config.getServletContext().getAttribute("validator");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processTemplate("index_not_authorized", req, resp, req.getServletContext());
    }
}
