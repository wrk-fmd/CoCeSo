package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.CaseDao;
import at.wrk.coceso.dao.CrewDao;
import at.wrk.coceso.dao.PoiDao;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Unit;
import at.wrk.coceso.entities.UnitState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UnitMapper implements RowMapper<Unit> {

    @Autowired
    CaseDao caseDao;

    @Autowired
    CrewDao crewDao;

    @Autowired
    PoiDao poiDao;

    @Override
    public Unit mapRow(ResultSet rs, int i) throws SQLException {
        Unit unit = new Unit();

        unit.id = rs.getInt("id");
        unit.ani = rs.getString("ani");
        unit.call = rs.getString("call");
        unit.aCase = caseDao.getById(rs.getInt("aCase"));
        unit.crew = crewDao.getByUnitId(unit.id);

        unit.home = poiDao.getById(rs.getInt("home"));
        unit.position = poiDao.getById(rs.getInt("position"));

        unit.info = rs.getString("info");
        unit.portable = rs.getBoolean("portable");
        unit.state = UnitState.valueOf(rs.getString("state"));
        unit.transportVehicle = rs.getBoolean("transportVehicle");
        unit.withDoc = rs.getBoolean("withDoc");

        return null;
    }
}
