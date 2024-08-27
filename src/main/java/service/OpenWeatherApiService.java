package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.GeocodingApiResponseDTO;
import dto.LocationDTO;
import dto.WeatherDTO;
import exception.server.OpenWeatherApiInteractionException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpenWeatherApiService {
    private final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_OPEN_WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String BASE_GEOCODING_API_URL = "http://api.openweathermap.org/geo/1.0/direct";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<WeatherDTO> getWeatherData(List<LocationDTO> locations) {
        try {
            List<WeatherDTO> weatherDtoList = new ArrayList<>();

            for (var location : locations) {
                String requestURL = BASE_OPEN_WEATHER_API_URL + "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + API_KEY + "&lang=en&units=metric";

                HttpRequest httpRequest = getHttpRequest(requestURL);

                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                statusCodeHandler(response.statusCode());

                WeatherDTO weatherDTO = objectMapper.readValue(response.body(), new TypeReference<>() {});
                weatherDTO.setName(location.getName()); // Set saved name from DB, because from API response it can be incorrect
                weatherDTO.setState(location.getState());

                weatherDtoList.add(weatherDTO);
            }
            return weatherDtoList;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new OpenWeatherApiInteractionException(e);
        }
    }
    public List<WeatherDTO> getWeatherData(String locationName) {
        try {
            Set<GeocodingApiResponseDTO> locations = getListOfLocationsByName(locationName);
            Set<WeatherDTO> weatherDTOs = new HashSet<>();
            for (var location : locations) {
                WeatherDTO weatherDTO = getWeatherByLatitudeAndLongitude(location.getLat(), location.getLon());
                weatherDTO.setState(location.getState()); // Set state manually because you can get it only from geocoding api
                weatherDTO.setName(location.getName()); // Set name manually from geocoding api because you can get incorrect name from OpenWeatherAPI

                weatherDTOs.add(weatherDTO);
            }
            return new ArrayList<>(weatherDTOs);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new OpenWeatherApiInteractionException(e);
        }
    }

    private Set<GeocodingApiResponseDTO> getListOfLocationsByName(String locationName) throws IOException, InterruptedException, URISyntaxException {
        String encodedLocationName = URLEncoder.encode(locationName, StandardCharsets.UTF_8);
        String requestURL = BASE_GEOCODING_API_URL + "?q=" + encodedLocationName + "&limit=5&appid=" + API_KEY;

        HttpRequest httpRequest = getHttpRequest(requestURL);

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        statusCodeHandler(response.statusCode());

        return objectMapper.readValue(response.body(), new TypeReference<>() {});
    }

    public WeatherDTO getWeatherByLatitudeAndLongitude(double lat, double lon) throws URISyntaxException, IOException, InterruptedException {
        String encodedLatitude = URLEncoder.encode(Double.toString(lat), StandardCharsets.UTF_8);
        String encodedLongitude = URLEncoder.encode(Double.toString(lon), StandardCharsets.UTF_8);
        String requestURL = BASE_OPEN_WEATHER_API_URL + "?lat=" + encodedLatitude + "&lon=" + encodedLongitude + "&lang=en&units=metric&appid=" + API_KEY;

        HttpRequest httpRequest = getHttpRequest(requestURL);

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        statusCodeHandler(response.statusCode());

        return objectMapper.readValue(response.body(), new TypeReference<>() {});
    }

    private static HttpRequest getHttpRequest(String requestURL) throws URISyntaxException {
        return HttpRequest.newBuilder()
                    .uri(new URI(requestURL))
                    .GET()
                .build();
    }

    private void statusCodeHandler(int statusCode) {
        int firstDigit = statusCode / 100;
        if (firstDigit == 4) {
            switch (statusCode) {
                case 429 ->
                        throw new OpenWeatherApiInteractionException("The limit of requests to OpenWeatherAPI has been reached");
                case 404 -> throw new OpenWeatherApiInteractionException("Your request or data is incorrect");
                default -> throw new OpenWeatherApiInteractionException();
            }
        } else if (firstDigit == 5) {
            throw new OpenWeatherApiInteractionException();
        }
    }
}