package at.wrk.coceso.dao.mapper;


import at.wrk.coceso.entities.CocesoPOI;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PoiMapper implements RowMapper<CocesoPOI> {
    @Override
    public CocesoPOI mapRow(ResultSet rs, int i) throws SQLException {
        CocesoPOI poi = new CocesoPOI();

        poi.id = rs.getInt("id");
        poi.address = rs.getString("address");
        poi.latitude = rs.getDouble("latitude");
        poi.longitude = rs.getDouble("longitude");
        poi.minimumUnits = rs.getInt("minimumunits");

        return poi;
    }
}
