package mapper;

import entity.Location;
import entity.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationRowMapper implements RowMapper<Location> {
    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Location.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .state(rs.getString("state"))
                .user(User.builder()
                        .id(rs.getLong("user_id"))
                        .build())
                .latitude(rs.getDouble("latitude"))
                .longitude(rs.getDouble("longitude"))
                .build();
    }
}
