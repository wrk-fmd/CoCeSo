package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.CrewDao;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.helper.UnitWithLocked;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.TaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UnitMapperWithLocked implements RowMapper<UnitWithLocked> {

    private final static
    Logger LOG = Logger.getLogger(UnitMapperWithLocked.class);

  @Autowired
  private CrewDao crewDao;

  @Autowired
  private PointService pointService;

  @Autowired
  private TaskService taskService;

  @Override
  public UnitWithLocked mapRow(ResultSet rs, int i) throws SQLException {
    UnitWithLocked unit = new UnitWithLocked();

    // Basic Datatype
    unit.setId(rs.getInt("id"));
    unit.setAni(rs.getString("ani"));
    unit.setCall(rs.getString("call"));
    unit.setInfo(rs.getString("info"));
    unit.setPortable(rs.getBoolean("portable"));
    unit.setTransportVehicle(rs.getBoolean("transportVehicle"));
    unit.setWithDoc(rs.getBoolean("withDoc"));
    unit.setLocked(rs.getBoolean("locked"));
    try {
      unit.setState(UnitState.valueOf(rs.getString("state")));
    } catch (IllegalArgumentException e) {
      LOG.error("UnitMapper: unit_id:" + unit.getId() + ", Cant read UnitState, Reset To NULL");
      unit.setState(null);
    }

    // References
    unit.setConcern(rs.getInt("concern_fk"));
    unit.setHome(pointService.getById(rs.getInt("home_point_fk")));
    unit.setPosition(pointService.getById(rs.getInt("position_point_fk")));

    // Extra Table
    unit.setCrew(crewDao.getByUnitId(unit.getId()));
    unit.setIncidents(taskService.getAllByUnitId(unit.getId()));

    return unit;
  }
}