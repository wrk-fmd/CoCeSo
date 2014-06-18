package at.wrk.coceso.dao;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.utils.CocesoLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class TaskDao {
    private JdbcTemplate jdbc;

    @Autowired
    public TaskDao(DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
    }

    public Map<Integer, TaskState> getAllByIncidentId(int id) {
        String q = "SELECT * FROM task WHERE incident_fk = ?";

        SqlRowSet rs = jdbc.queryForRowSet(q, id);

        Map<Integer, TaskState> ret = new HashMap<Integer, TaskState>();

        while(rs.next()) {
            ret.put(rs.getInt("unit_fk"), TaskState.valueOf(rs.getString("state")));
        }

        return ret;
    }

    public Map<Integer, TaskState> getAllByUnitId(int id) {
        String q = "SELECT * FROM task WHERE unit_fk = ?";

        SqlRowSet rs = jdbc.queryForRowSet(q, id);

        Map<Integer, TaskState> ret = new HashMap<Integer, TaskState>();

        while(rs.next()) {
            ret.put(rs.getInt("incident_fk"), TaskState.valueOf(rs.getString("state")));
        }

        return ret;
    }

    public List<Incident> getAllByUnitIdWithType(int id) {
        String q = "SELECT i.id, i.type, i.state FROM task t LEFT OUTER JOIN incident i ON t.incident_fk = i.id " +
                "WHERE t.unit_fk = ?";

        SqlRowSet rs = jdbc.queryForRowSet(q, id);

        List<Incident> ret = new LinkedList<Incident>();

        while(rs.next()) {

            Incident tmp = new Incident();
            tmp.setId(rs.getInt("id"));

            String x = rs.getString("type");
            tmp.setType((x == null ? null : IncidentType.valueOf(x)));
            x = rs.getString("state");
            tmp.setState((x == null ? null : IncidentState.valueOf(x)));

            ret.add(tmp);
        }

        return ret;
    }

    public boolean add(int incident_id, int unit_id, TaskState state) {
        String q = "INSERT INTO task (incident_fk, unit_fk, state) VALUES (?,?,?)";

        try {
            CocesoLogger.debug("TaskDao.add(): Try to add unit #" + unit_id +
                    ", incident #" + incident_id + " with new state '" + state + "'");
            jdbc.update(q, incident_id, unit_id, state.name());
        } catch(DataAccessException e) {
            CocesoLogger.warn("TaskDao add: "+e);
            return false;
        }
        return true;
    }

    public boolean update(int incident_id, int unit_id, TaskState state) {
        String q = "UPDATE task SET state = ? WHERE incident_fk = ? AND unit_fk = ?";

        try {
            CocesoLogger.debug("TaskDao.update(): Try to update unit #" + unit_id +
                    ", incident #" + incident_id + " to new state '" + state + "'");
            jdbc.update(q, state.name(), incident_id, unit_id);
        } catch(DataAccessException e) {
            CocesoLogger.warn("TaskDao.update(): ERROR "+e);
            return false;
        }
        return true;
    }

    public void remove(int incident_id, int unit_id) {
        String q = "DELETE FROM task WHERE incident_fk = ? AND unit_fk = ?";

        try {
            CocesoLogger.debug("TaskDao.remove(): Try to remove unit #" + unit_id +
                    " and incident #" + incident_id);
            jdbc.update(q, incident_id, unit_id);
        } catch(DataAccessException e) {
            CocesoLogger.warn("TaskDao.remove(): "+e);
        }
    }

    public void removeAllByUnit(int unit_id) {
        String q = "DELETE FROM task WHERE unit_fk = ?";

        try {
            CocesoLogger.debug("TaskDao.removeAllByUnit(): Try to remove all from unit #" + unit_id);
            jdbc.update(q, unit_id);
        } catch(DataAccessException e) {
            CocesoLogger.warn("TaskDao removeAllByUnit: "+e);
        }
    }

    public void removeAllByIncident(int incident_id) {
        String q = "DELETE FROM task WHERE incident_fk = ?";

        try {
            CocesoLogger.debug("TaskDao.removeAllByIncident(): Try to remove all from incident #" + incident_id);
            jdbc.update(q, incident_id);
        } catch(DataAccessException e) {
            CocesoLogger.debug("TaskDao removeAllByIncident: "+e);
        }
    }
}
