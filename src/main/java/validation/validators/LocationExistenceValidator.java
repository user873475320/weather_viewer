package validation.validators;

import dto.LocationDTO;
import lombok.RequiredArgsConstructor;
import service.LocationService;

@RequiredArgsConstructor
public class LocationExistenceValidator {

    private final LocationService locationService;

    public boolean isExist(LocationDTO locationDTO, Long userId) {
        return locationService.findUserLocationDTOs(userId)
                .stream()
                .anyMatch(location -> location.equals(locationDTO));
    }
}
