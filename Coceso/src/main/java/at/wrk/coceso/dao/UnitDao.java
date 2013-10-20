package at.wrk.coceso.dao;


import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
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

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Unit> getAll(Case caze) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean update(Unit unit) {

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean add(Unit unit) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
