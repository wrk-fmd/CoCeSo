package at.wrk.coceso.dao.mapper;


import at.wrk.coceso.dao.CaseDao;
import at.wrk.coceso.dao.PoiDao;
import at.wrk.coceso.entities.incidents.Incident;
import at.wrk.coceso.entities.incidents.IncidentState;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class IncidentMapper implements RowMapper<Incident> {

    @Autowired
    private CaseDao caseDao;

    @Autowired
    private PoiDao poiDao;

    @Override
    public Incident mapRow(ResultSet rs, int i) throws SQLException {
        Incident inc = new Incident();

        // Basic Datatypes
        inc.id = rs.getInt("id");
        inc.blue = rs.getBoolean("blue");
        inc.caller = rs.getString("caller");
        inc.casusNr = rs.getString("casusNr");
        inc.priority = rs.getInt("priority");
        inc.info = rs.getString("info");
        try {
            inc.state = IncidentState.valueOf(rs.getString("state"));
        }
        catch(IllegalArgumentException e) {
            Logger.error("IncidentMapper: incident_id:"+inc.id+", Cant read IncidentState, Reset To NULL");
            inc.state = null;
        }

        // References
        inc.aCase = caseDao.getById(rs.getInt("aCase"));
        inc.bo = poiDao.getById(rs.getInt("bo"));
        inc.ao = poiDao.getById(rs.getInt("ao"));

        return inc;
    }
}
