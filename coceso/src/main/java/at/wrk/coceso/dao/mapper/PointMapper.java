package at.wrk.coceso.dao.mapper;


import at.wrk.coceso.entity.Point;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PointMapper implements RowMapper<Point> {
    @Override
    public Point mapRow(ResultSet rs, int i) throws SQLException {
        Point poi = new Point();

        poi.setId(rs.getInt("id"));
        poi.setInfo(rs.getString("info"));
        poi.setLatitude(rs.getDouble("latitude"));
        poi.setLongitude(rs.getDouble("longitude"));

        return poi;
    }
}
