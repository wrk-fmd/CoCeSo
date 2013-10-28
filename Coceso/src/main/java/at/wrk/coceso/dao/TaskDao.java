package at.wrk.coceso.dao;

import at.wrk.coceso.entities.TaskState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
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

    public void add(int incident_id, int unit_id, TaskState state) {
        String q = "INSERT INTO tasks (incident_id, unit_id, state) VALUES (?,?,?)";

        jdbc.update(q, incident_id, unit_id, state.name());
    }

    public void update(int incident_id, int unit_id, TaskState state) {
        String q = "UPDATE tasks SET state = ? WHERE incident_id = ? AND unit_id = ?";

        jdbc.update(q, state.name(), incident_id, unit_id);
    }
}
