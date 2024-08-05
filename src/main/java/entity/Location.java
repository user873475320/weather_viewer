package entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "locations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @NotNull
    @Column(length = 200, nullable = false)
    private String name;

    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @DecimalMax(value = "90.0", message = "Latitude must be less or equal to 90.0")
    @DecimalMin(value = "-90.0", message = "Latitude must be more or equal to -90.0")
    @NotNull
    @Column(nullable = false)
    private Double latitude;

    @DecimalMax(value = "180.0", message = "Longitude must be less or equal to 180.0")
    @DecimalMin(value = "-180.0", message = "Longitude must be more or equal to -180.0")
    @NotNull
    @Column(nullable = false)
    private Double longitude;

}
