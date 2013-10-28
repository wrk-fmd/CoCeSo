package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.entities.Person;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CrewPersonMapper implements RowMapper<Integer> {
    @Override
    public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getInt("persons_id");
    }
}
