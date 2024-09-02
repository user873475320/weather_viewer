package dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    @Size(max = 300)
    @NotBlank
    private String name;

    @Size(max = 300)
    private String state;

    @DecimalMax(value = "90.0", message = "Latitude must be less or equal to 90.0")
    @DecimalMin(value = "-90.0", message = "Latitude must be more or equal to -90.0")
    @NotNull
    private Double latitude;

    @DecimalMax(value = "180.0", message = "Longitude must be less or equal to 180.0")
    @DecimalMin(value = "-180.0", message = "Longitude must be more or equal to -180.0")
    @NotNull
    private Double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDTO that = (LocationDTO) o;
        return Objects.equals(latitude, that.latitude) && Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}