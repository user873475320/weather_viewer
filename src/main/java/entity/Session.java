package entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Session {

    private UUID id;
    private User user;
    private LocalDateTime expiresAt;
}
