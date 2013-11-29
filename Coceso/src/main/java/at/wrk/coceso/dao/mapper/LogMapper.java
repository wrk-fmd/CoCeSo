package at.wrk.coceso.dao.mapper;

import at.wrk.coceso.dao.IncidentDao;
import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class LogMapper implements RowMapper<LogEntry> {

    @Autowired
    IncidentDao incidentDao;

    @Autowired
    UnitDao unitDao;

    @Override
    public LogEntry mapRow(ResultSet rs, int i) throws SQLException {
        LogEntry l = new LogEntry();

        l.id = rs.getInt("id");
        l.autoGenerated = rs.getBoolean("autoGenerated");
        l.text = rs.getString("text");
        l.timestamp = rs.getTimestamp("timestamp");
        l.json = rs.getString("json");

        try{
            l.state = TaskState.valueOf(rs.getString("taskstate"));
        }
        catch(NullPointerException e) {
            l.state = null;
        }


        l.user = new Person();
        l.user.id = rs.getInt("pid");
        l.user.sur_name = rs.getString("sur_name");
        l.user.given_name = rs.getString("given_name");
        l.user.dNr = rs.getInt("dNr");
        l.user.contact = rs.getString("contact");
        l.user.username = rs.getString("username");

        // References NOT RESOLVED
        int incidentID = rs.getInt("incident");
        if(incidentID > 0) {
            l.incident = new Incident();
            l.incident.id = incidentID;
        }

        int unitID = rs.getInt("unit");
        if(unitID > 0) {
            l.unit = new Unit();
            l.unit.id = unitID;
            l.unit.call = rs.getString("call");
        }

        //l.incident = incidentDao.getById(rs.getInt("incident"));
        //l.unit = unitDao.getById(rs.getInt("unit"));

        l.aCase = null; // Entries are 'final', aCase is only in DB relevant TODO Change if used internally

        return l;
    }
}
