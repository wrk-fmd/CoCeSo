package at.wrk.coceso.dao.mapper;


import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.entities.Concern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ConcernMapper implements RowMapper<Concern> {

    @Autowired
    private PointDao pointDao;

    @Override
    public Concern mapRow(ResultSet rs, int i) throws SQLException {
        Concern caze = new Concern();

        // Basic Datatypes
        caze.id = rs.getInt("id");
        caze.pax = rs.getInt("pax");
        caze.info = rs.getString("info");
        caze.name = rs.getString("name");
        caze.closed = rs.getBoolean("closed");

        caze.place = pointDao.getById(rs.getInt("point_fk"));

        return caze;
    }
}
