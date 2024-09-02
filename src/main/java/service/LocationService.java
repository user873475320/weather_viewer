package service;

import dao.LocationDAO;
import dto.LocationDTO;
import entity.Location;
import entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LocationService {

    private final LocationDAO locationDAO = new LocationDAO();

    public List<LocationDTO> findUserLocationDTOs(Long userId) {
        List<LocationDTO> locationDTOS = locationDAO.findLocationsByUserId(userId)
                .stream()
                .map(location -> new LocationDTO(location.getName(), location.getState(), location.getLatitude(), location.getLongitude()))
                .toList();

        log.debug("Found {} location(s) for user ID: {}", locationDTOS.size(), userId);
        return locationDTOS;
    }

    public void save(LocationDTO locationDTO, Long userId) {
        locationDAO.save(Location.builder()
                    .name(locationDTO.getName())
                    .state(locationDTO.getState())
                    .latitude(locationDTO.getLatitude())
                    .longitude(locationDTO.getLongitude())
                    .user(User.builder().id(userId).build())
                .build());
        log.debug("Location was saved: {} of user with ID: {}", locationDTO, userId);
    }

    public void delete(LocationDTO locationDTO, Long userId) {
        locationDAO.delete(Location.builder()
                    .name(locationDTO.getName())
                    .state(locationDTO.getState())
                    .latitude(locationDTO.getLatitude())
                    .longitude(locationDTO.getLongitude())
                    .user(User.builder().id(userId).build())
                .build());
        log.debug("Location was deleted: {} of user with ID: {}", locationDTO, userId);
    }
}