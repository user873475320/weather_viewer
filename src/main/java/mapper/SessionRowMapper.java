package mapper;

import entity.Session;
import entity.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SessionRowMapper implements RowMapper<Session> {
    @Override
    public Session mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Session.builder()
                .id(UUID.fromString(rs.getString("id")))
                .user(User.builder()
                        .id(rs.getLong("user_id"))
                        .build())
                .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
                .build();
    }
}
