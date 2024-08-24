package configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;

@WebListener
@Getter
@NoArgsConstructor
public class ThymeleafConfig {

    private TemplateEngine templateEngine;

    public ThymeleafConfig(ServletContext servletContext) {
        this.templateEngine = this.templateEngine(servletContext);
    }

    private TemplateEngine templateEngine(ServletContext servletContext) {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver(servletContext));

        return templateEngine;
    }

    private ITemplateResolver templateResolver(ServletContext servletContext) {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);

        return templateResolver;
    }

}
