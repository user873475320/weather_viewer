package validation.validators;

import dto.LocationDTO;
import service.LocationService;
import service.impl.LocationServiceImpl;

public class LocationExistenceValidator {

    private final LocationService locationService = new LocationServiceImpl();

    public boolean isExist(LocationDTO locationDTO, Long userId) {
        return locationService.findUserLocationDTOs(userId)
                .stream()
                .anyMatch(location -> location.equals(locationDTO));
    }
}
