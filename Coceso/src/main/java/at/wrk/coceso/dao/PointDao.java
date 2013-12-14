package at.wrk.coceso.dao;

import at.wrk.coceso.dao.mapper.PointMapper;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class PointDao extends CocesoDao<Point> {

    @Autowired
    PointMapper pointMapper;

    @Autowired
    public PointDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Point getById(int id) {
        if(id < 1) {
            Logger.error("PoiDao.getById(int): Invalid ID: " + id);
            return null;
        }

        String q = "select * from point where id = ?";
        Point poi;

        try {
            poi = jdbc.queryForObject(q, new Integer[] {id}, pointMapper);
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.getById(int): requested id: "+id+"; DataAccessException: "+dae.getMessage());
            return null;
        }

        return poi;
    }

    /**
     * Returns first Point with info == <code>info</code>
     * @param info String to search for
     * @return First Point
     */
    public Point getByInfo(String info) {
        if(info == null)
            return null;

        String q = "SELECT * FROM point WHERE info = ?";

        try {
            List<Point> list = jdbc.query(q, new Object[] {info}, pointMapper);
            if(list.isEmpty())
                return null;
            return list.get(0);
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.getByInfo: requested id: "+info+"; DataAccessException: "+dae.getMessage());
            return null;
        }
    }

    // Useless
    @Override
    @Deprecated
    public List<Point> getAll(int case_id) {
        throw new UnsupportedOperationException();
    }

    public List<Point> getAll() {
        String q = "select * from point";

        try {
            return jdbc.query(q, pointMapper);
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.getAll(): DataAccessException: "+dae.getMessage());
            return null;
        }

    }

    @Override
    public boolean update(Point poi) {
        if(poi == null) {
            Logger.error("PoiDao.update(): poi is NULL");
            return false;
        }
        if(poi.id <= 0) {
            Logger.error("PoiDao.update(): Invalid id: " + poi.id);
            return false;
        }

        String q = "UPDATE point SET info = ?, longitude = ?, latitude = ? " +
                "WHERE id = ?";

        try {
            jdbc.update(q, poi.info, poi.longitude, poi.latitude, poi.id);
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.update(): DataAccessException: " + dae.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public int add(final Point poi) {
        if(poi == null) {
            Logger.error("PoiDao.add(): poi is NULL");
            return -1;
        }

        poi.prepareNotNull();

        final String q = "INSERT INTO point (info, longitude, latitude) " +
                "VALUES (?, ?, ?)";

        try {
            KeyHolder holder = new GeneratedKeyHolder();

            jdbc.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, poi.info);
                    ps.setDouble(2, poi.longitude);
                    ps.setDouble(3, poi.latitude);

                    return ps;
                }
            }, holder);

            return (Integer) holder.getKeys().get("id");
        }
        catch(DataAccessException dae) {
            Logger.error("PoiDao.add(): DataAccessException: " + dae.getMessage());
            return -1;
        }
    }

    @Override
    public boolean remove(Point poi) {
        if(poi == null) {
            Logger.error("PoiDao.remove(): poi is NULL");
            return false;
        }
        if(poi.id <= 0) {
            Logger.error("PoiDao.remove(): Invalid id: " + poi.id);
            return false;
        }

        String q = "DELETE FROM point WHERE id = ?";

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
