package at.wrk.coceso.dao.mapper;


import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.entities.*;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class IncidentMapper implements RowMapper<Incident> {

    @Autowired
    private ConcernDao concernDao;

    @Autowired
    private PointDao pointDao;

    @Autowired
    private TaskDao taskDao;

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
        catch(NullPointerException e) {
            Logger.error("IncidentMapper: incident_id:"+inc.id+", Cant read IncidentState, Reset To NULL");
            inc.state = null;
        }
        try {
            inc.type = IncidentType.valueOf(rs.getString("type"));
        }
        catch(NullPointerException e) {
            Logger.error("IncidentMapper: incident_id:"+inc.id+", Cant read IncidentType, Reset To NULL");
            inc.type = null;
        }
        // References
        inc.concern = rs.getInt("concern_fk");
        inc.bo = pointDao.getById(rs.getInt("bo_point_fk"));
        inc.ao = pointDao.getById(rs.getInt("ao_point_fk"));
        inc.units = taskDao.getAllByIncidentId(inc.id);

        return inc;
    }
}
