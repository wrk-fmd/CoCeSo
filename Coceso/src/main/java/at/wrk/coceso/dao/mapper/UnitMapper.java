package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.dao.CrewDao;
import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.entities.Unit;
import at.wrk.coceso.entities.UnitState;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UnitMapper implements RowMapper<Unit> {

    @Autowired
    private ConcernDao concernDao;

    @Autowired
    private CrewDao crewDao;

    @Autowired
    private PointDao pointDao;

    @Autowired
    private TaskDao taskDao;

    @Override
    public Unit mapRow(ResultSet rs, int i) throws SQLException {
        Unit unit = new Unit();

        // Basic Datatype
        unit.id = rs.getInt("id");
        unit.ani = rs.getString("ani");
        unit.call = rs.getString("call");
        unit.info = rs.getString("info");
        unit.portable = rs.getBoolean("portable");
        unit.transportVehicle = rs.getBoolean("transportVehicle");
        unit.withDoc = rs.getBoolean("withDoc");
        try {
            unit.state = UnitState.valueOf(rs.getString("state"));
        }
        catch(IllegalArgumentException e) {
            Logger.error("IncidentMapper: incident_id:" + unit.id + ", Cant read UnitState, Reset To NULL");
            unit.state = null;
        }

        // References
        unit.concern = concernDao.getById(rs.getInt("concern_fk"));
        unit.home = pointDao.getById(rs.getInt("home_point_fk"));
        unit.position = pointDao.getById(rs.getInt("position_point_fk"));

        // Extra Table
        unit.crew = crewDao.getByUnitId(unit.id);

        unit.incidents = taskDao.getAllByUnitId(unit.id);

        return unit;
    }
}
