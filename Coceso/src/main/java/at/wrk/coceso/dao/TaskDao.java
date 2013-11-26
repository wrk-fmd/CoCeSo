package at.wrk.coceso.dao;

import at.wrk.coceso.entities.Incident;
import at.wrk.coceso.entities.IncidentType;
import at.wrk.coceso.entities.TaskState;
import at.wrk.coceso.utils.Logger;
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
        String q = "SELECT * FROM tasks WHERE incident_id = ?";

        SqlRowSet rs = jdbc.queryForRowSet(q, id);

        Map<Integer, TaskState> ret = new HashMap<Integer, TaskState>();

        while(rs.next()) {
            ret.put(rs.getInt("unit_id"), TaskState.valueOf(rs.getString("state")));
        }

        return ret;
    }

    public Map<Integer, TaskState> getAllByUnitId(int id) {
        String q = "SELECT * FROM tasks WHERE unit_id = ?";

        SqlRowSet rs = jdbc.queryForRowSet(q, id);

        Map<Integer, TaskState> ret = new HashMap<Integer, TaskState>();

        while(rs.next()) {
            ret.put(rs.getInt("incident_id"), TaskState.valueOf(rs.getString("state")));
        }

        return ret;
    }

    public List<Incident> getAllByUnitIdWithType(int id) {
        String q = "SELECT i.id, i.type FROM tasks t LEFT OUTER JOIN incidents i ON t.incident_id = i.id " +
                "WHERE t.unit_id = ?";

        SqlRowSet rs = jdbc.queryForRowSet(q, id);

        List<Incident> ret = new LinkedList<Incident>();

        while(rs.next()) {
            Incident tmp = new Incident();
            tmp.id = rs.getInt("id");
            tmp.type = IncidentType.valueOf(rs.getString("type"));

            ret.add(tmp);
        }

        return ret;
    }

    public boolean add(int incident_id, int unit_id, TaskState state) {
        String q = "INSERT INTO tasks (incident_id, unit_id, state) VALUES (?,?,?)";

        try {
            jdbc.update(q, incident_id, unit_id, state.name());
        } catch(DataAccessException e) {
            Logger.debug("TaskDao add: "+e);
            return false;
        }
        return true;
    }

    public boolean update(int incident_id, int unit_id, TaskState state) {
        String q = "UPDATE tasks SET state = ? WHERE incident_id = ? AND unit_id = ?";

        try {
            jdbc.update(q, state.name(), incident_id, unit_id);
        } catch(DataAccessException e) {
            Logger.debug("TaskDao update: "+e);
            return false;
        }
        return true;
    }

    public void remove(int incident_id, int unit_id) {
        String q = "DELETE FROM tasks WHERE incident_id = ? AND unit_id = ?";

        try {
            jdbc.update(q, incident_id, unit_id);
        } catch(DataAccessException e) {
            Logger.debug("TaskDao remove: "+e);
        }
    }

    public void removeAll(int unit_id) {
        String q = "DELETE FROM tasks WHERE unit_id = ?";

        try {
            jdbc.update(q, unit_id);
        } catch(DataAccessException e) {
            Logger.debug("TaskDao removeAll: "+e);
        }
    }
}
