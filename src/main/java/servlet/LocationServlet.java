package servlet;

import dto.LocationDTO;
import entity.Session;
import exception.client.InvalidUserRequestException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.thymeleaf.TemplateEngine;
import service.LocationService;
import service.WeatherService;
import util.ExceptionHandler;
import util.HttpSessionUtils;
import util.LocationUtils;
import validation.validators.LocationExistenceValidator;

import java.io.IOException;
import java.util.Set;

@WebServlet("/location")
public class LocationServlet extends BaseServlet {

    private LocationExistenceValidator locationExistenceValidator;
    private WeatherService weatherService;
    private LocationService locationService;

    @Override
    public void init(ServletConfig config) {
        exceptionHandler = (ExceptionHandler) config.getServletContext().getAttribute("exceptionHandler");
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        validator = (Validator) config.getServletContext().getAttribute("validator");

        locationService = (LocationService) config.getServletContext().getAttribute("locationService");
        weatherService = (WeatherService) config.getServletContext().getAttribute("weatherService");
        locationExistenceValidator = new LocationExistenceValidator(locationService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = HttpSessionUtils.getSessionFromHttpSession(req.getSession());

        LocationDTO locationDTO = LocationUtils.getUserLoginDtoOnlyWithNameFromRequest(req);
        Set<ConstraintViolation<LocationDTO>> violations = validator.validate(locationDTO);

        if (!violations.isEmpty()) {
            throw new InvalidUserRequestException("Data is incorrect(name too long)", HttpServletResponse.SC_BAD_REQUEST);
        }

        req.setAttribute("login", session.getUser().getLogin());
        req.setAttribute("weatherDtoList", weatherService.getWeatherData(locationDTO.getName()));
        processTemplate("search_results", req, resp, req.getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Session session = HttpSessionUtils.getSessionFromHttpSession(req.getSession());

        LocationDTO locationDTO = LocationUtils.getUserLoginDtoFromRequest(req);
        Set<ConstraintViolation<LocationDTO>> violations = validator.validate(locationDTO);
        boolean isExist = locationExistenceValidator.isExist(locationDTO, session.getUser().getId());

        if (!violations.isEmpty() || isExist) {
            throw new InvalidUserRequestException("Location already exists or location data is incorrect", HttpServletResponse.SC_BAD_REQUEST, true);
        }

        locationService.save(locationDTO, session.getUser().getId());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Session session = HttpSessionUtils.getSessionFromHttpSession(req.getSession());

        LocationDTO locationDTO = LocationUtils.getUserLoginDtoFromRequest(req);
        Set<ConstraintViolation<LocationDTO>> violations = validator.validate(locationDTO);
        boolean isExist = locationExistenceValidator.isExist(locationDTO, session.getUser().getId());

        if (!violations.isEmpty() || !isExist) {
            throw new InvalidUserRequestException("Location doesn't exist or location data is incorrect", HttpServletResponse.SC_BAD_REQUEST, true);
        }

        locationService.delete(locationDTO, session.getUser().getId());
    }
}