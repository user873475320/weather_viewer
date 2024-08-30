package servlet;

import dto.LocationDTO;
import entity.Session;
import exception.client.InvalidUserRequestException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import service.LocationService;
import service.OpenWeatherApiService;
import util.HttpSessionUtils;
import util.LocationUtils;
import validation.validators.LocationExistenceValidator;

import java.io.IOException;
import java.util.Set;

@WebServlet("/location")
public class LocationServlet extends BaseServlet {

    private final LocationExistenceValidator locationExistenceValidator = new LocationExistenceValidator();
    private final LocationService locationService = new LocationService();
    private final OpenWeatherApiService openWeatherApiService = new OpenWeatherApiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = HttpSessionUtils.getSessionFromHttpSession(req.getSession());

        LocationDTO locationDTO = LocationUtils.getUserLoginDtoOnlyWithNameFromRequest(req);
        Set<ConstraintViolation<LocationDTO>> violations = validator.validate(locationDTO);

        if (!violations.isEmpty()) {
            throw new InvalidUserRequestException("Data is incorrect(name too long)", HttpServletResponse.SC_BAD_REQUEST);
        }

        context.setVariable("login", session.getUser().getLogin());
        context.setVariable("weatherDtoList", openWeatherApiService.getWeatherData(locationDTO.getName()));
        templateEngine.process("search_results", context, resp.getWriter());
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