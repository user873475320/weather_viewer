package service;

import dto.LocationDTO;

import java.util.List;

public interface LocationService {

    List<LocationDTO> findUserLocationDTOs(Long userId);

    void save(LocationDTO locationDTO, Long userId);

    void delete(LocationDTO locationDTO, Long userId);
}
