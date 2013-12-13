package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.ConcernMapper;
import at.wrk.coceso.entities.Concern;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ConcernDao extends CocesoDao<Concern> {

    private final String prefix = "select * from concern ";

    @Autowired
    ConcernMapper concernMapper;

    @Autowired
    public ConcernDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Concern getById(int id) {
        if(id < 1) {
            Logger.error("CaseDao.getById(int): Invalid ID: " + id);
            return null;
        }

        String q = prefix+"where id = ?";
        Concern caze;

        try {
            caze = jdbc.queryForObject(q, new Integer[] {id}, concernMapper);
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
    public List<Concern> getAll(int case_id) {
        throw new UnsupportedOperationException();
    }


    public List<Concern> getAll() {
        String q = prefix;

        try {
            return jdbc.query(q, concernMapper);
        }
        catch(DataAccessException dae) {
            Logger.error("UnitDao.getAll: DataAccessException: "+dae.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(Concern caze) {
        if(caze == null) return false;

        String q = "UPDATE concern SET name = ?, point_fk = ?, info = ?, pax = ?, closed = ? WHERE id = ?";

        try {
            jdbc.update(q, caze.name, caze.place == null ? null : caze.place.id,
                    caze.info, caze.pax, caze.closed, caze.id);
            return true;
        }
        catch (DataAccessException dae) {
            return false;
        }
    }


    @Override
    public int add(Concern caze) {
        if(caze == null) return -1;

        caze.prepareNotNull();

        String q = "INSERT INTO concern (name, point_fk, info, pax) VALUES (?, ?, ?, ?)";

        try {
            jdbc.update(q, caze.name, caze.place == null ? null : caze.place.id, caze.info, caze.pax);
            return 0;
        }
        catch (DataAccessException dae) {
            return -1;
        }

    }

    /**
     * USE WITH CAUTION, CASCADING IS ENABLED!
     */
    @Override
    public boolean remove(Concern caze) {
        if(caze == null) return false;

        String q = "DELETE FROM concern WHERE id = ?";

        jdbc.update(q, caze.id);
        return false;
    }
}
