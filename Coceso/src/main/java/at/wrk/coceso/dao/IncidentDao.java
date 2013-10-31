package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.IncidentMapper;
import at.wrk.coceso.entities.Incident;
import at.wrk.coceso.entities.IncidentState;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IncidentDao extends CocesoDao<Incident> {

    @Autowired
    private IncidentMapper incidentMapper;

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

        String q = "SELECT * FROM incidents WHERE id = ?";
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

        String q = "SELECT * FROM incidents WHERE acase = ? ORDER BY id ASC";

        return jdbc.query(q, new Object[] {case_id}, incidentMapper);
    }

    public List<Incident> getAllActive(int case_id) {
        if(case_id <= 0) {
            return null;
        }

        String q = "SELECT * FROM incidents WHERE acase = ? AND state NOT IN ('Done') ORDER BY id ASC";

        return jdbc.query(q, new Object[] {case_id}, incidentMapper);
    }

    /**
     * Incident.priority and .blue are written on every method-call! All other vars only if != NULL
     * @param incident Incident to update in DB
     * @return Success of Operation
     */
    @Override
    public boolean update(Incident incident) {
        if(incident == null) return false;

        final String pre_q = "UPDATE incidents SET ";
        final String suf_q = "WHERE id = ?";

        String q = pre_q;
        List<Object> parameters = new ArrayList<Object>();
        boolean comma = false;

        if(incident.caller != null) {
            q += "caller = ? ";
            parameters.add(incident.caller);
            comma = true;
        }
        if(incident.ao != null) {
            if(comma) q+= ",";
            q += "ao = ? ";
            parameters.add(incident.ao.id);
            comma = true;
        }
        if(incident.bo != null) {
            if(comma) q+= ",";
            q += "bo = ? ";
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

        q += (comma ? "," : "")+"priority = ?, blue = ? ";
        parameters.add(incident.priority);
        parameters.add(incident.blue);

        parameters.add(incident.id);

        q += suf_q;

        jdbc.update(q, parameters.toArray());

        return true;
    }

    @Override
    public boolean add(Incident incident) {
        if (incident == null || incident.aCase == null) return false;

        String q = "INSERT INTO incidents (acase, state, type, priority, blue, bo, ao, info, caller, casusnr) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        jdbc.update(q, incident.aCase.id,
                incident.state == null ? null : incident.state.name(),
                incident.type == null ? null : incident.type.name(),
                incident.priority, incident.blue, incident.bo, incident.ao, incident.info, incident.caller,
                incident.casusNr);

        return true;
    }

    @Override
    public boolean remove(Incident incident) {
        if(incident == null) return false;
        String q = "DELETE FROM incidents WHERE id = ?";

        jdbc.update(q, incident.id);

        return true;
    }
}
