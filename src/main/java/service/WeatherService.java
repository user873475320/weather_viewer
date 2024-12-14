package service;

import dto.LocationDTO;
import dto.WeatherDTO;

import java.util.List;

public interface WeatherService {
    List<WeatherDTO> getWeatherData(List<LocationDTO> locations);

    List<WeatherDTO> getWeatherData(String locationName);
}
