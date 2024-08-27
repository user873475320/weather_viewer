package util;

import dto.LocationDTO;
import exception.client.InvalidUserRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocationUtils {
    private LocationUtils() {
    }

    public static LocationDTO getUserLoginDtoFromRequest(HttpServletRequest request) {
        String locationName = request.getParameter("name");
        String locationState = request.getParameter("state");
        String locationLatitude = request.getParameter("latitude");
        String locationLongitude = request.getParameter("longitude");

        if (locationName == null || locationLatitude == null || locationLongitude == null) {
            throw new InvalidUserRequestException("Some of the parameters are missing", HttpServletResponse.SC_BAD_REQUEST);
        }

        if (locationState != null && locationState.equals("null")) {
            locationState = null;
        }

        try {
            return new LocationDTO(locationName, locationState, Double.parseDouble(locationLatitude), Double.parseDouble(locationLongitude));
        } catch (NumberFormatException e) {
            throw new InvalidUserRequestException("Can't parse latitude or longitude from request", HttpServletResponse.SC_BAD_REQUEST, e);
        }
    }

    public static LocationDTO getUserLoginDtoOnlyWithNameFromRequest(HttpServletRequest request) {
        String locationName = request.getParameter("name");
        if (locationName == null) {
            throw new InvalidUserRequestException("'name' parameter was not found", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Fill latitude and longitude with some CORRECT(successfully pass validation) data
        return new LocationDTO(locationName, null, 0.0, 0.0);
    }
}
