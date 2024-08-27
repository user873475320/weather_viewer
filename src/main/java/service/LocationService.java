package service;

import dao.LocationDAO;
import dto.LocationDTO;
import entity.Location;
import entity.User;

import java.util.List;

public class LocationService {

    private final LocationDAO locationDAO = new LocationDAO();

    public List<LocationDTO> findUserLocationDTOs(Long userId) {
        return locationDAO.findLocationsByUserId(userId)
                .stream()
                .map(location -> new LocationDTO(location.getName(), location.getState(),location.getLatitude(), location.getLongitude()))
                .toList();
    }

    public void saveLocation(LocationDTO locationDTO, Long userId) {
        locationDAO.save(Location.builder()
                    .name(locationDTO.getName())
                    .state(locationDTO.getState())
                    .latitude(locationDTO.getLatitude())
                    .longitude(locationDTO.getLongitude())
                    .user(User.builder().id(userId).build())
                .build());
    }

    public void deleteLocation(LocationDTO locationDTO, Long userId) {
        locationDAO.delete(Location.builder()
                    .name(locationDTO.getName())
                    .state(locationDTO.getState())
                    .latitude(locationDTO.getLatitude())
                    .longitude(locationDTO.getLongitude())
                    .user(User.builder().id(userId).build())
                .build());
    }
}