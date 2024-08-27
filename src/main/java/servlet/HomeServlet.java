package servlet;

import dto.LocationDTO;
import dto.WeatherDTO;
import entity.Session;
import service.LocationService;
import service.OpenWeatherApiService;
import util.HttpSessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends BaseServlet {

    private final OpenWeatherApiService openWeatherApiService = new OpenWeatherApiService();
    private final LocationService locationService = new LocationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = HttpSessionUtils.getSessionFromHttpSession(req.getSession());

        List<LocationDTO> userLocationDtoList = locationService.findUserLocationDTOs(session.getUser().getId());
        List<WeatherDTO> weatherDtoList = openWeatherApiService.getWeatherData(userLocationDtoList);

        context.setVariable("login", session.getUser().getLogin());
        context.setVariable("weatherDtoList", weatherDtoList);
        templateEngine.process("index_authorized", context, resp.getWriter());
    }
}