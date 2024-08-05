package entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Size(min = 3, max = 100, message = "Login length must be between 3 and 100 characters")
    @NotBlank(message = "Username can not be empty")
    @Column(nullable = false, length = 100, unique = true)
    private String login;

    @Size(min = 6, max = 100, message = "Password length must be between 6 and 100 characters")
    @NotBlank(message = "Password can not be empty")
    @Column(nullable = false, length = 100)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Location> locations = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Session> sessions = new ArrayList<>();

    @Override
    public String toString() {
        return id + " " + login + " " + password;
    }
}
