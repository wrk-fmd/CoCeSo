package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.UnitMapper;
import at.wrk.coceso.entity.Person;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.UnitState;
import org.apache.log4j.Logger;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UnitDao extends CocesoDao<Unit> {

    private final static
    Logger LOG = Logger.getLogger("CoCeSo");

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private CrewDao crewDao;

    @Autowired
    private PointDao pointDao;

    @Autowired
    private LogDao logDao;

    @Autowired
    public UnitDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Unit getById(int id) {
        if(id < 1) {
            LOG.error("Invalid ID: " + id);
            return null;
        }

        String q = "SELECT * FROM unit WHERE id = ?";
        Unit unit;

        try {
            unit = jdbc.queryForObject(q, new Integer[] {id}, unitMapper);
        }
        catch(DataAccessException dae) {
            LOG.error("requested id: " + id + "; DataAccessException: " + dae.getMessage());
            return null;
        }

        return unit;
    }

    @Override
    public List<Unit> getAll(int concern_id) {
        if(concern_id < 1) {
            LOG.warn("invalid concern_id: " + concern_id);
            return null;
        }
        String q = "SELECT * FROM unit WHERE concern_fk = ? ORDER BY id ASC";

        try {
            return jdbc.query(q, new Object[] {concern_id}, unitMapper);
        }
        catch(DataAccessException dae) {
            LOG.error(dae.getMessage());
            return null;
        }
    }

    //TODO move to PointService
    Point createPointIfNotExist(Point dummy) {
        if(dummy == null)
            return null;

        // Marker for deletion
        if(dummy.getId() == -2) {
            return dummy;
        }

        // If the id already exists, return Point from Database
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
     * Update Unit. Only Values state, info, position are changeable. All others are LOCKED!
     * To change these, use updateFull(Unit).
     *
     * @param unit Unit to write to DB
     * @return Success of Operation
     */
    @Override
    public boolean update(Unit unit) {
        if(unit == null) {
            LOG.error("unit is NULL");
            return false;
        }
        if(unit.getId() <= 0) {
            LOG.error("Invalid id: " + unit.getId() + ", call: " + unit.getCall());
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
        if(unit.getPosition() != null) {
            if(!first) {
                q += ",";
            }
            q += " position_point_fk = " + (unit.getPosition().getId() == -2 ? "null" : unit.getPosition().getId());
            first = false;
        }
        /*if(unit.getHome() != null) {
            if(!first) {
                q += ",";
            }
            q += " home_point_fk = " + (unit.getHome().getId() == -2;
            // first = false;
        }*/
        q += suf_q;

        // Nothing to update
        if(first) {
            LOG.warn("Tried to update empty Unit: id=" + unit.getId());
            return false;
        }

        try {
            if(info_given) {
                jdbc.update(q, unit.getInfo());
            }
            else {
                jdbc.update(q);
            }
        }
        catch(DataAccessException dae) {
            LOG.error("DataAccessException: " + dae.getMessage());
            return false;
        }
        return true;
    }

    public boolean updateFull(Unit unit) {
        if(unit == null) {
            LOG.error("unit is NULL");
            return false;
        }
        if(unit.getId() <= 0) {
            LOG.error("Invalid id: " + unit.getId() + ", call: " + unit.getCall());
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
                    unit.getHome() == null || unit.getHome().getId() <= 0 ? null : unit.getHome().getId(),
                    unit.getId());
        }
        catch(DataAccessException dae) {
            LOG.error("DataAccessException: " + dae.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public int add(Unit uunit) {
        if(uunit == null) {
            LOG.error("unit is NULL");
            return -1;
        }
        if(uunit.getConcern() == null || uunit.getConcern() <= 0) {
            LOG.error("No concern given. call: " + uunit.getCall());
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
            LOG.error("call: " + unit.getCall() + "; DataAccessException: " + dae.getMessage());
            return -1;
        }

    }

    @Override
    public boolean remove(Unit unit) {
        if(unit == null) {
            LOG.error("unit is NULL");
            return false;
        }
        if(unit.getId() <= 0) {
            LOG.error("invalid id: " + unit.getId() + ", call: " + unit.getCall());
            return false;
        }

        // Load from DB for Security Reasons. => Concern cannot be faked and all fields are loaded
        LOG.info("Load current unit from DB");
        unit = getById(unit.getId());


        if(getNonDeletable(unit.getConcern()).contains(unit.getId())) {
            LOG.warn("tried to remove non-deletable Unit");
            return false;
        }
        logDao.updateForRemoval(unit.getId());

        String q = "delete from unit where id = ?";
        try {
            jdbc.update(q, unit.getId());
        }
        catch (DataAccessException dae) {
            LOG.debug("UnitDao.remove(Unit): id: " + unit.getId() + "; DataAccessException: " + dae.getMessage());
            return false;
        }

        LOG.info("Unit removed. ID=" + unit.getId());
        return true;
    }

    /**
     * Returns Integer-Set of Unit IDs, that are already used by the Main Application and cannot be removed anymore
     * @param concernId ID of Concern, in that all Units are checked
     * @return Integer-Set of Unit IDs
     */
    public Set<Integer> getNonDeletable(int concernId) {
        String q = "SELECT DISTINCT u.id FROM unit u, log l WHERE u.id = l.unit_fk AND l.concern_fk = ? " +
                "AND (l.type != 'UNIT_CREATE' OR l.type IS NULL)";

        SqlRowSet rs = jdbc.queryForRowSet(q, concernId);

        Set<Integer> ret = new HashSet<Integer>();

        while(rs.next()) {
            ret.add(rs.getInt("id"));
        }

        return ret;
    }
}
