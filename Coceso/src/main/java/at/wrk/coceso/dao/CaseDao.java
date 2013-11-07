package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.CaseMapper;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CaseDao extends CocesoDao<Case> {

    @Autowired
    CaseMapper caseMapper;

    @Autowired
    public CaseDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Case getById(int id) {
        if(id < 1) {
            Logger.error("CaseDao.getById(int): Invalid ID: " + id);
            return null;
        }

        String q = "select * from cases where id = ?";
        Case caze;

        try {
            caze = jdbc.queryForObject(q, new Integer[] {id}, caseMapper);
        }
        catch(IncorrectResultSizeDataAccessException e) {
            Logger.error("CaseDao.getById(int): requested id: "+id
                    +"; IncorrectResultSizeDataAccessException: "+e.getMessage());
            return null;
        }
        catch(DataAccessException dae) {
            Logger.error("CaseDao.getById(int): requested id: "+id+"; DataAccessException: "+dae.getMessage());
            return null;
        }

        return caze;
    }

    // Useless
    @Deprecated
    @Override
    public List<Case> getAll(int case_id) {
        //throw new NotImplementedException();
        throw new UnsupportedOperationException();
    }


    public List<Case> getAll() {
        String q = "select * from cases";

        try {
            return jdbc.query(q, caseMapper);
        }
        catch(DataAccessException dae) {
            Logger.error("UnitDao.getAll: DataAccessException: "+dae.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(Case caze) {
        if(caze == null) return false;

        String q = "UPDATE cases SET name = ?, place = ?, organiser = ?, pax = ? WHERE id = ?";

        try {
            jdbc.update(q, caze.name, caze.place == null ? null : caze.place.id, caze.organiser, caze.pax, caze.id);
            return true;
        }
        catch (DataAccessException dae) {
            return false;
        }
    }


    @Override
    public boolean add(Case caze) {
        if(caze == null) return false;

        caze.prepareNotNull();

        String q = "INSERT INTO cases (name, place, organiser, pax) VALUES (?, ?, ?, ?)";

        try {
            jdbc.update(q, caze.name, caze.place == null ? null : caze.place.id, caze.organiser, caze.pax);
            return true;
        }
        catch (DataAccessException dae) {
            return false;
        }

    }

    /**
     * USE WITH CAUTION, CASCADING IS ENABLED!
     */
    @Override
    public boolean remove(Case caze) {
        if(caze == null) return false;

        String q = "DELETE FROM cases WHERE id = ?";

        jdbc.update(q, caze.id);
        return false;
    }
}
