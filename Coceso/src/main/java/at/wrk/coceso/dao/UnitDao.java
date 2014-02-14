package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.UnitMapper;
import at.wrk.coceso.entity.Person;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Repository
public class UnitDao extends CocesoDao<Unit> {

    @Autowired
    UnitMapper unitMapper;

    @Autowired
    CrewDao crewDao;

    @Autowired
    PointDao pointDao;

    @Autowired
    LogDao logDao;

    @Autowired
    public UnitDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Unit getById(int id) {
        if(id < 1) {
            Logger.error("UnitDao.getById(int): Invalid ID: " + id);
            return null;
        }

        String q = "select * from unit where id = ?";
        Unit unit;

        try {
            unit = jdbc.queryForObject(q, new Integer[] {id}, unitMapper);
        }
        catch(DataAccessException dae) {
            Logger.error("UnitDao.getById(int): requested id: "+id+"; DataAccessException: "+dae.getMessage());
            return null;
        }

        return unit;
    }

    @Override
    public List<Unit> getAll(int concern_id) {
        if(concern_id < 1) {
            Logger.warning("UnitDao.getAll: invalid concern_id: "+concern_id);
            return null;
        }
        String q = "SELECT * FROM unit WHERE concern_fk = ? ORDER BY id ASC";

        try {
            return jdbc.query(q, new Object[] {concern_id}, unitMapper);
        }
        catch(DataAccessException dae) {
            Logger.error("UnitDao.getAll: DataAccessException: "+dae.getMessage());
            return null;
        }
    }

    //TODO move to PointService
    Point createPointIfNotExist(Point dummy) {
        if(dummy == null)
            return null;

        if(dummy.getId() > 0) {
            Point p = pointDao.getById(dummy.getId());
            if(p != null)
                return p;
        }


        Point point = pointDao.getByInfo(dummy.getInfo());
        if(point == null && dummy.getInfo() != null && !dummy.getInfo().isEmpty()) {
            dummy.setId(pointDao.add(dummy));
            return dummy;
        }
        else return point;
    }

    /**
     * Update Unit. Only Values state, info, position, home are changeable. All others are LOCKED!
     * To change these, use updateFull(Unit).
     *
     * @param unit Unit to write to DB
     * @return Success of Operation
     */
    @Override
    public boolean update(Unit unit) {
        if(unit == null) {
            Logger.error("UnitDao.update(Unit): unit is NULL");
            return false;
        }
        if(unit.getId() <= 0) {
            Logger.error("UnitDao.update(Unit): Invalid id: " + unit.getId() + ", call: "+ unit.getCall());
            return false;
        }

        unit.setHome(createPointIfNotExist(unit.getHome()));
        unit.setPosition(createPointIfNotExist(unit.getPosition()));

        final String pre_q = "update unit set";
        final String suf_q = " where id = " + unit.getId();

        boolean first = true;
        boolean info_given = false;

        String q = pre_q;
        if(unit.getState() != null) {
            q += " state = '" + unit.getState().name() + "'";
            first = false;
        }
        if(unit.getInfo() != null) {
            if(!first) {
                q += ",";
            }
            q += " info = ?";
            info_given = true;
            first = false;
        }
        if(unit.getPosition() != null && unit.getPosition().getId() > 0) {
            if(!first) {
                q += ",";
            }
            q += " position_point_fk = " + unit.getPosition().getId();
            first = false;
        }
        if(unit.getHome() != null && unit.getHome().getId() > 0) {
            if(!first) {
                q += ",";
            }
            q += " home_point_fk = " + unit.getHome().getId();
            // first = false;
        }
        q += suf_q;
        try {
            if(info_given) {
                jdbc.update(q, unit.getInfo());
            }
            else {
                jdbc.update(q);
            }
        }
        catch(DataAccessException dae) {
            Logger.error("UnitDao.update(Unit): DataAccessException: " + dae.getMessage());
            return false;
        }
        return true;
    }

    public boolean updateFull(Unit unit) {
        if(unit == null) {
            Logger.error("UnitDao.updateFull(Unit): unit is NULL");
            return false;
        }
        if(unit.getId() <= 0) {
            Logger.error("UnitDao.updateFull(Unit): Invalid id: " + unit.getId() + ", call: "+ unit.getCall());
            return false;
        }

        unit.setHome(createPointIfNotExist(unit.getHome()));
        unit.setPosition(createPointIfNotExist(unit.getPosition()));


        String q = "UPDATE unit SET state = ?, call = ?, ani = ?, withdoc = ?, " +
                "portable = ?, transportvehicle = ?, info = ?, position_point_fk = ?, home_point_fk = ? WHERE id = ?";

        try {
            jdbc.update(q,
                    unit.getState() == null ? UnitState.AD.name() : unit.getState().name(),
                    unit.getCall(),
                    unit.getAni(),
                    unit.isWithDoc(),
                    unit.isPortable(),
                    unit.isTransportVehicle(),
                    unit.getInfo(),
                    unit.getPosition() == null || unit.getPosition().getId() <= 0 ? null : unit.getPosition().getId(),
                    unit.getHome() == null || unit.getHome().getId() <= 0 ? null : unit.getHome().getId(), unit.getId());
        }
        catch(DataAccessException dae) {
            Logger.error("UnitDao.updateFull(Unit): DataAccessException: " + dae.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public int add(Unit uunit) {
        if(uunit == null) {
            Logger.error("UnitDao.add(Unit): unit is NULL");
            return -1;
        }
        if(uunit.getConcern() == null || uunit.getConcern() <= 0) {
            Logger.error("UnitDao.add(Unit): No concern given. call: " + uunit.getCall());
            return -1;
        }

        uunit.prepareNotNull();

        uunit.setHome(createPointIfNotExist(uunit.getHome()));
        uunit.setPosition(createPointIfNotExist(uunit.getPosition()));


        final Unit unit = uunit;

        try {
            final String q = "INSERT INTO unit (concern_fk, state, call, ani, withDoc," +
                    " portable, transportVehicle, info, position_point_fk, home_point_fk) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            KeyHolder holder = new GeneratedKeyHolder();

            jdbc.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                    ps.setInt(1, unit.getConcern());
                    ps.setString(2, unit.getState() == null ? UnitState.AD.name() : unit.getState().name());
                    ps.setString(3, unit.getCall());
                    ps.setString(4, unit.getAni());
                    ps.setBoolean(5, unit.isWithDoc());
                    ps.setBoolean(6, unit.isPortable());
                    ps.setBoolean(7, unit.isTransportVehicle());
                    ps.setString(8, unit.getInfo());
                    if(unit.getPosition() == null || unit.getPosition().getId() <= 0)
                        ps.setObject(9,  null);
                    else
                        ps.setInt(9, unit.getPosition().getId());

                    if(unit.getHome() == null || unit.getHome().getId() <= 0)
                        ps.setObject(10,  null);
                    else
                        ps.setInt(10, unit.getHome().getId());
                    return ps;
                }
            }, holder);

            if(unit.getCrew() != null) {
                for(Person p : unit.getCrew()) {
                    crewDao.add(unit, p);
                }
            }
            return (Integer) holder.getKeys().get("id");
        }
        catch (DataAccessException dae) {
            Logger.error("UnitDao.add(Unit): call: "+ unit.getCall() +"; DataAccessException: "+dae.getMessage());
            return -1;
        }

    }

    @Override
    public boolean remove(Unit unit) {
        if(unit == null) {
            Logger.error("UnitDao.remove(Unit): unit is NULL");
            return false;
        }
        if(unit.getId() <= 0) {
            Logger.error("UnitDao.remove(Unit): invalid id: " + unit.getId() + ", call: " + unit.getCall());
            return false;
        }
        if(getNonDeletable(unit.getConcern()).contains(unit.getId())) {
            return false;
        }
        logDao.updateForRemoval(unit.getId());

        String q = "delete from unit where id = ?";
        try {
            jdbc.update(q, unit.getId());
        }
        catch (DataAccessException dae) {
            Logger.debug("UnitDao.remove(Unit): id: " + unit.getId() + "; DataAccessException: " + dae.getMessage());
            return false;
        }

        return true;
    }

    public Set<Integer> getNonDeletable(int caseId) {
        String q = "SELECT DISTINCT u.id FROM unit u, log l WHERE u.id = l.unit_fk AND l.concern_fk = ? " +
                "AND l.type != 'UNIT_CREATE'";

        SqlRowSet rs = jdbc.queryForRowSet(q, caseId);

        Set<Integer> ret = new HashSet<Integer>();

        while(rs.next()) {
            ret.add(rs.getInt("id"));
        }

        return ret;
    }
}
