package at.wrk.coceso.dao;

import at.wrk.coceso.entities.CocesoPOI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PoiDao extends CocesoDao<CocesoPOI> {

    @Autowired
    public PoiDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CocesoPOI getById(int id) {
        return null;
    }

    @Override
    public List<CocesoPOI> getAll(int case_id) {
        return null;
    }

    @Override
    public boolean update(CocesoPOI cocesoPOI) {
        return false;
    }

    @Override
    public boolean add(CocesoPOI cocesoPOI) {
        return false;
    }

    @Override
    public boolean remove(CocesoPOI cocesoPOI) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
