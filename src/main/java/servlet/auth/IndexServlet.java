package servlet.auth;

import configuration.ThymeleafConfig;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import util.ExceptionHandler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {

    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateEngine templateEngine = new ThymeleafConfig(getServletContext()).getTemplateEngine();
            WebContext context = new WebContext(req, resp, getServletContext(), req.getLocale());
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("index_not_authorized", context, resp.getWriter());
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
}
