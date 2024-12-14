package servlet;

import dto.LocationDTO;
import dto.WeatherDTO;
import entity.Session;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import org.thymeleaf.TemplateEngine;
import service.LocationService;
import service.WeatherService;
import util.ExceptionHandler;
import util.HttpSessionUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends BaseServlet {

    private WeatherService weatherService;
    private LocationService locationService;

    @Override
    public void init(ServletConfig config) {
        exceptionHandler = (ExceptionHandler) config.getServletContext().getAttribute("exceptionHandler");
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        validator = (Validator) config.getServletContext().getAttribute("validator");

        locationService = (LocationService) config.getServletContext().getAttribute("locationService");
        weatherService = (WeatherService) config.getServletContext().getAttribute("weatherService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = HttpSessionUtils.getSessionFromHttpSession(req.getSession());

        List<LocationDTO> userLocationDtoList = locationService.findUserLocationDTOs(session.getUser().getId());
        List<WeatherDTO> weatherDtoList = weatherService.getWeatherData(userLocationDtoList);

        req.setAttribute("login", session.getUser().getLogin());
        req.setAttribute("weatherDtoList", weatherDtoList);
        processTemplate("index_authorized", req, resp, req.getServletContext());
    }
}