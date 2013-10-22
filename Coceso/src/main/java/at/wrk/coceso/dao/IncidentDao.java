package at.wrk.coceso.dao;

import at.wrk.coceso.entities.incidents.Incident;
import org.springframework.beans.factory.annotation.Autowired;
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
        return null;
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
