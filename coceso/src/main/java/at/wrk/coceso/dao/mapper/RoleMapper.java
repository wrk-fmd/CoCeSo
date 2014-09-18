package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.entity.enums.CocesoAuthority;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RoleMapper implements RowMapper<CocesoAuthority> {

    private final static
    Logger LOG = Logger.getLogger(RoleMapper.class);

    @Override
    public CocesoAuthority mapRow(ResultSet resultSet, int i) throws SQLException {
        String role = resultSet.getString("role");
        try {
            return CocesoAuthority.valueOf(role);
        } catch (Exception e) {
            LOG.error(String.format("invalid role: %s", role));
            return null;
        }
    }
}
