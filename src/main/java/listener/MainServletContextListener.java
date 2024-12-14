package listener;

import configuration.ThymeleafConfig;
import dao.LocationRepository;
import dao.SessionRepository;
import dao.UserRepository;
import dao.impl.LocationRepositoryImpl;
import dao.impl.SessionRepositoryImpl;
import dao.impl.UserRepositoryImpl;
import exception.server.ServletInitializationException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import mapper.LocationRowMapper;
import mapper.SessionRowMapper;
import mapper.UserRowMapper;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.thymeleaf.TemplateEngine;
import service.LocationService;
import service.SessionService;
import service.UserService;
import service.WeatherService;
import service.impl.LocationServiceImpl;
import service.impl.OpenWeatherApiService;
import service.impl.SessionServiceImpl;
import service.impl.UserServiceImpl;
import util.ExceptionHandler;
import validation.validators.UniqueLoginValidator;

import javax.sql.DataSource;

@Slf4j
public class MainServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        DataSource dataSource = dataSource();
        context.setAttribute("dataSource", dataSource);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        context.setAttribute("jdbcTemplate", jdbcTemplate);

        TemplateEngine templateEngine = new ThymeleafConfig().templateEngine(context);
        context.setAttribute("templateEngine", templateEngine);

        ExceptionHandler exceptionHandler = new ExceptionHandler(context, templateEngine);
        context.setAttribute("exceptionHandler", exceptionHandler);

        try (var validatorFactory = Validation.buildDefaultValidatorFactory()){
            Validator validator = validatorFactory.getValidator();
            context.setAttribute("validator", validator);
        } catch (Exception e) {
            exceptionHandler.handle(new ServletInitializationException("Failed to initialize a servlet", e));
        }


        UserRowMapper userRowMapper = new UserRowMapper();
        context.setAttribute("userRowMapper", userRowMapper);

        LocationRowMapper locationRowMapper = new LocationRowMapper();
        context.setAttribute("locationRowMapper", locationRowMapper);

        SessionRowMapper sessionRowMapper = new SessionRowMapper();
        context.setAttribute("sessionRowMapper", sessionRowMapper);


        UserRepository userRepository = new UserRepositoryImpl(jdbcTemplate);
        context.setAttribute("userRepository", userRepository);

        LocationRepository locationRepository = new LocationRepositoryImpl(jdbcTemplate, locationRowMapper);
        context.setAttribute("locationRepository", locationRepository);

        SessionRepository sessionRepository = new SessionRepositoryImpl(jdbcTemplate);
        context.setAttribute("sessionRepository", sessionRepository);


        UserService userService = new UserServiceImpl(userRepository);
        context.setAttribute("userService", userService);

        LocationService locationService = new LocationServiceImpl(locationRepository);
        context.setAttribute("locationService", locationService);

        SessionService sessionService = new SessionServiceImpl(sessionRepository);
        context.setAttribute("sessionService", sessionService);

        WeatherService weatherService = new OpenWeatherApiService();
        context.setAttribute("weatherService", weatherService);


        UniqueLoginValidator.setUserRepository(userRepository);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("-=-=-=-=-=-=-=-=- CONTEXT DESTROYED -==-=-=-=-=-=-=-=-=");
    }

    private DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/weatherViewerDB");
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }
}
