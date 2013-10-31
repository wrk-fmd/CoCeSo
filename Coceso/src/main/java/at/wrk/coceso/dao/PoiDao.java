package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.PoiMapper;
import at.wrk.coceso.entities.CocesoPOI;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PoiDao extends CocesoDao<CocesoPOI> {

    @Autowired
    PoiMapper poiMapper;

    @Autowired
    public PoiDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CocesoPOI getById(int id) {
        if(id < 1) {
            Logger.error("PoiDao.getById(int): Invalid ID: " + id);
            return null;
        }

        String q = "select * from cocesopois where id = ?";
        CocesoPOI poi;

        try {
            poi = jdbc.queryForObject(q, new Integer[] {id}, poiMapper);
        }
        catch(IncorrectResultSizeDataAccessException e) {
            Logger.error("PoiDao.getById(int): requested id: "+id
                    +"; IncorrectResultSizeDataAccessException: "+e.getMessage());
            return null;
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.getById(int): requested id: "+id+"; DataAccessException: "+dae.getMessage());
            return null;
        }

        return poi;
    }

    // Useless
    @Override
    @Deprecated
    public List<CocesoPOI> getAll(int case_id) {
        throw new NotImplementedException();
    }

    public List<CocesoPOI> getAll() {
        String q = "select * from cocesopois";

        try {
            return jdbc.query(q, poiMapper);
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.getAll(): DataAccessException: "+dae.getMessage());
            return null;
        }

    }

    @Override
    public boolean update(CocesoPOI poi) {
        if(poi == null) {
            Logger.error("PoiDao.update(): poi is NULL");
            return false;
        }
        if(poi.id <= 0) {
            Logger.error("PoiDao.update(): Invalid id: " + poi.id);
            return false;
        }

        String q = "UPDATE cocesopois SET address = ?, longitude = ?, latitude = ?, minimumunits = ? " +
                "WHERE id = ?";

        try {
            jdbc.update(q, poi.address, poi.longitude, poi.latitude, poi.minimumUnits, poi.id);
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.update(): DataAccessException: " + dae.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean add(CocesoPOI poi) {
        if(poi == null) {
            Logger.error("PoiDao.add(): poi is NULL");
            return false;
        }

        poi.prepareNotNull();

        String q = "INSERT INTO cocesopois (address, longitude, latitude, minimumunits) " +
                "VALUES (?, ?, ?, ?)";

        try {
            jdbc.update(q, poi.address, poi.longitude, poi.latitude, poi.minimumUnits);
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.add(): DataAccessException: " + dae.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(CocesoPOI poi) {
        if(poi == null) {
            Logger.error("PoiDao.remove(): poi is NULL");
            return false;
        }
        if(poi.id <= 0) {
            Logger.error("PoiDao.remove(): Invalid id: " + poi.id);
            return false;
        }

        String q = "DELETE FROM cocesopois WHERE id = ?";

        try {
            jdbc.update(q, poi.id);
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.update(): DataAccessException: " + dae.getMessage());
            return false;
        }
        return true;
    }
}
