package at.wrk.coceso.dao.mapper;


import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.entity.Concern;
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
        caze.setId(rs.getInt("id"));
        caze.setPax(rs.getInt("pax"));
        caze.setInfo(rs.getString("info"));
        caze.setName(rs.getString("name"));
        caze.setClosed(rs.getBoolean("closed"));

        caze.setPlace(pointDao.getById(rs.getInt("point_fk")));

        return caze;
    }
}
