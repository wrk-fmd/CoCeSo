package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.UnitMapper;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UnitDao extends CocesoDao<Unit> {

    @Autowired
    public UnitDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Unit getById(int id) {
        if(id < 1) return null;

        Unit unit;

        try {
            unit = jdbc.queryForObject("select * from units where id = ?", new Object[] {id}, new UnitMapper());
        } catch(IncorrectResultSizeDataAccessException e) {
            unit = null;
        } catch(DataAccessException dae) {
            unit = null;
        }

        return unit;
    }

    @Override
    public List<Unit> getAll(int case_id) {
        try {
            return jdbc.query("select * from units where aCase = " + case_id, new UnitMapper());
        } catch(DataAccessException dae) {
                return null;
        }
    }

    @Override
    public boolean update(Unit unit) {

        return false;
    }

    @Override
    public boolean add(Unit unit) {
        return false;
    }

    public boolean sendHome(int id) {

        return false;
    }
}
