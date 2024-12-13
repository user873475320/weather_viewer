package entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Location {

    private Long id;
    private String name;
    private String state;
    private User user;
    private Double latitude;
    private Double longitude;
}
