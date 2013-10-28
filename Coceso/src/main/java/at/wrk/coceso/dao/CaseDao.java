package at.wrk.coceso.dao;

import at.wrk.coceso.entities.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CaseDao extends CocesoDao<Case> {

    @Autowired
    public CaseDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Case getById(int id) {
        return null;
    }

    @Override
    public List<Case> getAll(int case_id) {
        return null;
    }

    @Override
    public boolean update(Case aCase) {
        return false;
    }

    @Override
    public boolean updateFull(Case aCase) {
        return false;
    }

    @Override
    public boolean add(Case aCase) {
        return false;
    }

    @Override
    public boolean remove(Case aCase) {
        return false;
    }
}
