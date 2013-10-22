package at.wrk.coceso.dao;


import at.wrk.coceso.dao.mapper.UnitMapper;
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

        String q = "select * from units where id = ?";
        Unit unit;

        try {
            unit = jdbc.queryForObject(q, new Object[] {id}, new UnitMapper());
        } catch(IncorrectResultSizeDataAccessException e) {
            unit = null;
        } catch(DataAccessException dae) {
            unit = null;
        }

        return unit;
    }

    @Override
    public List<Unit> getAll(int case_id) {
        String q = "select * from units where aCase = " + case_id;

        try {
            return jdbc.query(q, new UnitMapper());
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
        if(unit.aCase == null) return false;

        try {
            if(unit.home == null && unit.position == null){
                String q = "insert into units (aCase, state, call, ani, withDoc, portable, transportVehicle, info) values (?, ?, ?, ?, ?, ?, ?, ?)";
                jdbc.update(q, unit.aCase.id, unit.state, unit.call, unit.ani, unit.withDoc, unit.portable, unit.transportVehicle, unit.info);

            }
            else if(unit.home == null) {
                String q = "insert into units (aCase, state, call, ani, withDoc, portable, transportVehicle, info, position) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                jdbc.update(q, unit.aCase.id, unit.state, unit.call, unit.ani, unit.withDoc, unit.portable, unit.transportVehicle, unit.info, unit.position.id);
            }
            else if(unit.position == null) {
                String q = "insert into units (aCase, state, call, ani, withDoc, portable, transportVehicle, info, home) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                jdbc.update(q, unit.aCase.id, unit.state, unit.call, unit.ani, unit.withDoc, unit.portable, unit.transportVehicle, unit.info, unit.home.id);
            }
            else {
                String q = "insert into units (aCase, state, call, ani, withDoc, portable, transportVehicle, info, position, home) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                jdbc.update(q, unit.aCase.id, unit.state, unit.call, unit.ani, unit.withDoc, unit.portable, unit.transportVehicle, unit.info, unit.position.id, unit.home.id);
            }
            return true;
        } catch (DataAccessException dae) {
            return false;
        }

    }

    @Override
    public boolean remove(Unit unit) {
        return false;
    }

    public Unit sendHome(int id) {

        return null;
    }
}
