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
    PointDao pointDao;

    @Autowired
    UnitDao unitDao;

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

        incident.bo = unitDao.createPointIfNotExist(incident.bo);
        incident.ao = unitDao.createPointIfNotExist(incident.ao);

        if(incident.caller != null) {
            q += "caller = ? ";
            parameters.add(incident.caller);
            comma = true;
        }
        if(incident.ao != null) {
            if(comma) q+= ",";
            q += "ao_point_fk = ? ";
            parameters.add(incident.ao.id);
            comma = true;
        }
        if(incident.bo != null) {
            if(comma) q+= ",";
            q += "bo_point_fk = ? ";
            parameters.add(incident.bo.id);
            comma = true;
        }
        if(incident.casusNr != null) {
            if(comma) q+= ",";
            q += "casusnr = ? ";
            parameters.add(incident.casusNr);
            comma = true;
        }
        if(incident.info != null) {
            if(comma) q+= ",";
            q += "info = ? ";
            parameters.add(incident.info);
            comma = true;
        }
        if(incident.state != null) {
            if(comma) q+= ",";
            q += "state = ? ";
            parameters.add(incident.state.name());
            comma = true;
        }
        if(incident.type != null) {
            if(comma) q+= ",";
            q += "type = ? ";
            parameters.add(incident.type.name());
            comma = true;
        }
        if(incident.priority != null) {
            if(comma) q+= ",";
            q += "priority = ? ";
            parameters.add(incident.priority);
            comma = true;
        }
        if(incident.blue != null) {
            if(comma) q+= ",";
            q += "blue = ? ";
            parameters.add(incident.blue);
            comma = true;
        }

        parameters.add(incident.id);

        q += suf_q;

        jdbc.update(q, parameters.toArray());

        return true;
    }

    //TODO Default IncidentType is now TASK
    @Override
    public int add(final Incident incident) {
        if (incident == null || incident.concern == null) return -1;

        final String q = "INSERT INTO incident (concern_fk, state, type, priority, blue, bo_point_fk, " +
                "ao_point_fk, info, caller, casusnr) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        KeyHolder holder = new GeneratedKeyHolder();

        incident.bo = unitDao.createPointIfNotExist(incident.bo);
        incident.ao = unitDao.createPointIfNotExist(incident.ao);

        jdbc.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                ps.setInt(1, incident.concern);
                ps.setString(2, incident.state == null ? IncidentState.New.name() : incident.state.name());
                ps.setString(3, incident.type == null ? IncidentType.Task.name() : incident.type.name());
                ps.setInt(4, incident.priority == null ? 0 : incident.priority);
                ps.setBoolean(5, incident.blue == null ? false : incident.blue);

                if(incident.bo != null)
                    ps.setInt(6, incident.bo.id);
                else
                    ps.setObject(6, null);

                if(incident.ao != null)
                    ps.setInt(7, incident.ao.id);
                else
                    ps.setObject(7, null);

                ps.setString(8, incident.info);
                ps.setString(9, incident.caller);
                ps.setString(10, incident.casusNr);
                return ps;
            }
        }, holder);



        return (Integer) holder.getKeys().get("id");
    }

    @Override
    public boolean remove(Incident incident) {
        if(incident == null) return false;
        String q = "DELETE FROM incident WHERE id = ?";

        jdbc.update(q, incident.id);

        return true;
    }
}
