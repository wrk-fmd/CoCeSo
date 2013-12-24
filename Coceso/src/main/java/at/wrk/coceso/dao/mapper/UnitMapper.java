package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.dao.CrewDao;
import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.dao.TaskDao;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.UnitState;
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
        unit.setId(rs.getInt("id"));
        unit.setAni(rs.getString("ani"));
        unit.setCall(rs.getString("call"));
        unit.setInfo(rs.getString("info"));
        unit.setPortable(rs.getBoolean("portable"));
        unit.setTransportVehicle(rs.getBoolean("transportVehicle"));
        unit.setWithDoc(rs.getBoolean("withDoc"));
        try {
            unit.setState(UnitState.valueOf(rs.getString("state")));
        }
        catch(IllegalArgumentException e) {
            Logger.error("IncidentMapper: incident_id:" + unit.getId() + ", Cant read UnitState, Reset To NULL");
            unit.setState(null);
        }

        // References
        unit.setConcern(rs.getInt("concern_fk"));
        unit.setHome(pointDao.getById(rs.getInt("home_point_fk")));
        unit.setPosition(pointDao.getById(rs.getInt("position_point_fk")));

        // Extra Table
        unit.setCrew(crewDao.getByUnitId(unit.getId()));

        unit.setIncidents(taskDao.getAllByUnitId(unit.getId()));

        return unit;
    }
}
