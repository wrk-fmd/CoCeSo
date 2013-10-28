package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.IncidentMapper;
import at.wrk.coceso.entities.incidents.Incident;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class IncidentDao extends CocesoDao<Incident> {

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

        String q = "select * from incidents where id = ?";
        Incident incident;

        try {
            incident = jdbc.queryForObject(q, new Object[] {id}, new IncidentMapper());
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
        return null;
    }

    @Override
    public boolean update(Incident incident) {
        return false;
    }

    @Override
    public boolean add(Incident incident) {
        return false;
    }

    @Override
    public boolean remove(Incident incident) {
        return false;
    }
}
