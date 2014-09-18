package at.wrk.coceso.dao.mapper;


import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class IncidentMapper implements RowMapper<Incident> {

    private final static
    Logger LOG = Logger.getLogger(IncidentMapper.class);

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
        inc.setId(rs.getInt("id"));
        inc.setBlue(rs.getBoolean("blue"));
        inc.setCaller(rs.getString("caller"));
        inc.setCasusNr(rs.getString("casusNr"));
        inc.setPriority(rs.getInt("priority"));
        inc.setInfo(rs.getString("info"));
        try {
            inc.setState(IncidentState.valueOf(rs.getString("state")));
        }
        catch(NullPointerException e) {
            LOG.error("IncidentMapper: incident_id:"+inc.getId()+", Cant read IncidentState, Reset To NULL");
            inc.setState(null);
        }
        try {
            inc.setType(IncidentType.valueOf(rs.getString("type")));
        }
        catch(NullPointerException e) {
            LOG.error("IncidentMapper: incident_id:"+inc.getId()+", Cant read IncidentType, Reset To NULL");
            inc.setType(null);
        }
        // References
        inc.setConcern(rs.getInt("concern_fk"));
        inc.setBo(pointDao.getById(rs.getInt("bo_point_fk")));
        inc.setAo(pointDao.getById(rs.getInt("ao_point_fk")));
        inc.setUnits(taskDao.getAllByIncidentId(inc.getId()));

        return inc;
    }
}
