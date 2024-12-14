package service.impl;

import dao.LocationRepository;
import dto.LocationDTO;
import entity.Location;
import entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.LocationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public List<LocationDTO> findUserLocationDTOs(Long userId) {
        List<LocationDTO> locationDTOs = locationRepository.findLocationsByUserId(userId)
                .stream()
                .map(location -> new LocationDTO(location.getName(), location.getState(), location.getLatitude(), location.getLongitude()))
                .toList();

        log.debug("Found {} location(s) for user ID: {}", locationDTOs.size(), userId);
        return locationDTOs;
    }

    @Override
    public void save(LocationDTO locationDTO, Long userId) {
        locationRepository.save(Location.builder()
                    .name(locationDTO.getName())
                    .state(locationDTO.getState())
                    .latitude(locationDTO.getLatitude())
                    .longitude(locationDTO.getLongitude())
                    .user(User.builder().id(userId).build())
                .build());
        log.debug("Location was saved: {} of user with ID: {}", locationDTO, userId);
    }

    @Override
    public void delete(LocationDTO locationDTO, Long userId) {
        locationRepository.delete(Location.builder()
                    .name(locationDTO.getName())
                    .state(locationDTO.getState())
                    .latitude(locationDTO.getLatitude())
                    .longitude(locationDTO.getLongitude())
                    .user(User.builder().id(userId).build())
                .build());
        log.debug("Location was deleted: {} of user with ID: {}", locationDTO, userId);
    }
}