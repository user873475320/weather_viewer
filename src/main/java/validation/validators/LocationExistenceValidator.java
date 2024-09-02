package validation.validators;

import dto.LocationDTO;
import service.LocationService;

public class LocationExistenceValidator {

    private final LocationService locationService = new LocationService();

    public boolean isExist(LocationDTO locationDTO, Long userId) {
        return locationService.findUserLocationDTOs(userId)
                .stream()
                .anyMatch(location -> location.equals(locationDTO));
    }
}
