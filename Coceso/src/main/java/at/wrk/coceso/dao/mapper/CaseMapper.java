package at.wrk.coceso.dao.mapper;


import at.wrk.coceso.dao.PoiDao;
import at.wrk.coceso.entities.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CaseMapper implements RowMapper<Case> {

    @Autowired
    private PoiDao poiDao;

    @Override
    public Case mapRow(ResultSet rs, int i) throws SQLException {
        Case caze = new Case();

        // Basic Datatypes
        caze.id = rs.getInt("id");
        caze.pax = rs.getInt("pax");
        caze.organiser = rs.getString("organiser");
        caze.name = rs.getString("name");

        caze.place = poiDao.getById(rs.getInt("place"));

        return caze;
    }
}
