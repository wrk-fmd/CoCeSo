package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.IncidentMapper;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IncidentDao extends CocesoDao<Incident> {

    @Autowired
    private IncidentMapper incidentMapper;

    @Autowired
    private PointDao pointDao;

    @Autowired
    private UnitDao unitDao;

    @Autowired
    public IncidentDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Incident getById(int id) {
        if(id < 1) {
            Logger.error("IncidentDao.getById(int): Invalid ID: "+id);
            return null;
        }

        String q = "SELECT * FROM incident WHERE id = ?";
        Incident incident;

        try {
            incident = jdbc.queryForObject(q, new Object[] {id}, incidentMapper);
        }
        catch(IncorrectResultSizeDataAccessException e) {
            Logger.error("IncidentDao.getById(int): requested id: " + id + "; "+e.getMessage());
            return null;
        }
        catch(DataAccessException dae) {
            Logger.error("IncidentDao.getById(int): requested id: "+id+"; DataAccessException: "+dae.getMessage());
            return null;
        }

        return incident;
    }

    @Override
    public List<Incident> getAll(int case_id) {
        if(case_id <= 0) {
            return null;
        }

        String q = "SELECT * FROM incident WHERE concern_fk = ? ORDER BY id ASC";

        return jdbc.query(q, new Object[] {case_id}, incidentMapper);
    }

    public List<Incident> getAllActive(int case_id) {
        if(case_id <= 0) {
            return null;
        }

        String q = "SELECT * FROM incident WHERE concern_fk = ? AND state NOT IN ('Done') ORDER BY id ASC";

        return jdbc.query(q, new Object[] {case_id}, incidentMapper);
    }

    public List<Incident> getAllByState(int case_id, IncidentState state) {
        if(case_id <= 0 || state == null) {
            return null;
        }

        String q = "SELECT * FROM incident WHERE concern_fk = ? AND state = ? ORDER BY id ASC";

        return jdbc.query(q, new Object[] {case_id, state.name()}, incidentMapper);
    }


    /**
     * Incident.priority and .blue are written on every method-call! All other vars only if != NULL
     * @param incident Incident to update in DB
     * @return Success of Operation
     */
    @Override
    public boolean update(Incident incident) {
        if(incident == null) return false;

        final String pre_q = "UPDATE incident SET ";
        final String suf_q = "WHERE id = ?";

        String q = pre_q;
        List<Object> parameters = new ArrayList<Object>();
        boolean comma = false;

        incident.setBo(unitDao.createPointIfNotExist(incident.getBo()));
        incident.setAo(unitDao.createPointIfNotExist(incident.getAo()));

        if(incident.getCaller() != null) {
            q += "caller = ? ";
            parameters.add(incident.getCaller());
            comma = true;
        }
        if(incident.getAo() != null) {
            if(comma) q+= ",";
            q += "ao_point_fk = ? ";
            parameters.add(incident.getAo().getId());
            comma = true;
        }
        if(incident.getBo() != null) {
            if(comma) q+= ",";
            q += "bo_point_fk = ? ";
            parameters.add(incident.getBo().getId());
            comma = true;
        }
        if(incident.getCasusNr() != null) {
            if(comma) q+= ",";
            q += "casusnr = ? ";
            parameters.add(incident.getCasusNr());
            comma = true;
        }
        if(incident.getInfo() != null) {
            if(comma) q+= ",";
            q += "info = ? ";
            parameters.add(incident.getInfo());
            comma = true;
        }
        if(incident.getState() != null) {
            if(comma) q+= ",";
            q += "state = ? ";
            parameters.add(incident.getState().name());
            comma = true;
        }
        if(incident.getType() != null) {
            if(comma) q+= ",";
            q += "type = ? ";
            parameters.add(incident.getType().name());
            comma = true;
        }
        if(incident.getPriority() != null) {
            if(comma) q+= ",";
            q += "priority = ? ";
            parameters.add(incident.getPriority());
            comma = true;
        }
        if(incident.getBlue() != null) {
            if(comma) q+= ",";
            q += "blue = ? ";
            parameters.add(incident.getBlue());
            comma = true;
        }

        parameters.add(incident.getId());

        q += suf_q;

        jdbc.update(q, parameters.toArray());

        return true;
    }

    //TODO Default IncidentType is now TASK
    @Override
    public int add(final Incident incident) {
        if (incident == null || incident.getConcern() == null) return -1;

        final String q = "INSERT INTO incident (concern_fk, state, type, priority, blue, bo_point_fk, " +
                "ao_point_fk, info, caller, casusnr) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        KeyHolder holder = new GeneratedKeyHolder();

        incident.setBo(unitDao.createPointIfNotExist(incident.getBo()));
        incident.setAo(unitDao.createPointIfNotExist(incident.getAo()));

        jdbc.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                ps.setInt(1, incident.getConcern());
                ps.setString(2, incident.getState() == null ? IncidentState.New.name() : incident.getState().name());
                ps.setString(3, incident.getType() == null ? IncidentType.Task.name() : incident.getType().name());
                ps.setInt(4, incident.getPriority() == null ? 0 : incident.getPriority());
                ps.setBoolean(5, incident.getBlue() == null ? false : incident.getBlue());

                if(incident.getBo() != null)
                    ps.setInt(6, incident.getBo().getId());
                else
                    ps.setObject(6, null);

                if(incident.getAo() != null)
                    ps.setInt(7, incident.getAo().getId());
                else
                    ps.setObject(7, null);

                ps.setString(8, incident.getInfo());
                ps.setString(9, incident.getCaller());
                ps.setString(10, incident.getCasusNr());
                return ps;
            }
        }, holder);



        return (Integer) holder.getKeys().get("id");
    }

    @Override
    public boolean remove(Incident incident) {
        if(incident == null) return false;
        String q = "DELETE FROM incident WHERE id = ?";

        jdbc.update(q, incident.getId());

        return true;
    }
}
