package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.utils.CocesoLogger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RoleMapper implements RowMapper<CocesoAuthority> {
    @Override
    public CocesoAuthority mapRow(ResultSet resultSet, int i) throws SQLException {
        String role = resultSet.getString("role");
        try {
            return CocesoAuthority.valueOf(role);
        } catch (Exception e) {
            CocesoLogger.error("RoleMapper: invalid: "+role);
            return null;
        }
    }
}
