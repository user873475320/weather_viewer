package servlet;

import dto.LocationDTO;
import dto.WeatherDTO;
import entity.Session;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.LocationService;
import service.WeatherService;
import service.impl.LocationServiceImpl;
import service.impl.OpenWeatherApiService;
import util.HttpSessionUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends BaseServlet {

    private final WeatherService weatherService = new OpenWeatherApiService();
    private final LocationService locationService = new LocationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = HttpSessionUtils.getSessionFromHttpSession(req.getSession());

        List<LocationDTO> userLocationDtoList = locationService.findUserLocationDTOs(session.getUser().getId());
        List<WeatherDTO> weatherDtoList = weatherService.getWeatherData(userLocationDtoList);

        req.setAttribute("login", session.getUser().getLogin());
        req.setAttribute("weatherDtoList", weatherDtoList);
        processTemplate("index_authorized", req, resp);
    }
}