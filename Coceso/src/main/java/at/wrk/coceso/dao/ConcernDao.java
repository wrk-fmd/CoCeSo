package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.ConcernMapper;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.utils.CocesoLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class ConcernDao extends CocesoDao<Concern> {

    private final String prefix = "SELECT * FROM concern ";

    @Autowired
    ConcernMapper concernMapper;

    @Autowired
    public ConcernDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Concern getById(int id) {
        if(id < 1) {
            //Logger.debug("CaseDao.getById(int): Invalid ID: " + id);
            return null;
        }

        String q = prefix+"WHERE id = ?";
        Concern caze;

        try {
            caze = jdbc.queryForObject(q, new Integer[] {id}, concernMapper);
        }
        catch(IncorrectResultSizeDataAccessException e) {
            CocesoLogger.debug("ConcernDao.getById(int): requested id: "+id
                    +"; IncorrectResultSizeDataAccessException: "+e.getMessage());
            return null;
        }
        catch(DataAccessException dae) {
            CocesoLogger.warning("CaseDao.getById(int): requested id: "+id+"; DataAccessException: "+dae.getMessage());
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
            CocesoLogger.error("UnitDao.getAll: DataAccessException: "+dae.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(Concern caze) {
        if(caze == null) return false;

        String q = "UPDATE concern SET name = ?, point_fk = ?, info = ?, pax = ?, closed = ? WHERE id = ?";

        try {
            jdbc.update(q, caze.getName(), caze.getPlace() == null ? null : caze.getPlace().getId(),
                    caze.getInfo(), caze.getPax(), caze.isClosed(), caze.getId());
            return true;
        }
        catch (DataAccessException dae) {
            return false;
        }
    }


    @Override
    public int add(final Concern concern) {
        if(concern == null) return -1;

        concern.prepareNotNull();

        final String q = "INSERT INTO concern (name, point_fk, info, pax) VALUES (?, ?, ?, ?)";

        try {
            KeyHolder holder = new GeneratedKeyHolder();

            jdbc.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, concern.getName());

                    if(concern.getPlace() == null)
                        ps.setObject(2, null);
                    else
                        ps.setInt(2, concern.getPlace().getId());

                    ps.setString(3, concern.getInfo());
                    ps.setInt(4, concern.getPax());
                    return ps;
                }
            }, holder);

            return (Integer) holder.getKeys().get("id");
        }
        catch (DataAccessException dae) {
            return -1;
        }

    }

    /**
     * USE WITH CAUTION, CASCADING IS ENABLED!
     */
    @Override
    @PreAuthorize("denyAll") // TODO Change Right Management if used
    public boolean remove(Concern caze) {
        if(caze == null) return false;

        String q = "DELETE FROM concern WHERE id = ?";

        jdbc.update(q, caze.getId());
        return false;
    }

    public List<Concern> getAllActive() {
        String q = prefix + " WHERE closed = false";

        try {
            return jdbc.query(q, concernMapper);
        }
        catch(DataAccessException dae) {
            CocesoLogger.error("UnitDao.getAll: DataAccessException: "+dae.getMessage());
            return null;
        }
    }
}
