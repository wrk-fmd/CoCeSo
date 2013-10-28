package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.entities.Unit;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CrewUnitMapper implements RowMapper<Integer> {
    @Override
    public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getInt("units_id");
    }
}
