package at.wrk.coceso.entity;

import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.JsonContainer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class LogEntry {

    private int id;

    private Concern concern;

    private Timestamp timestamp;

    private Unit unit;

    private Incident incident;

    private TaskState state;

    private LogEntryType type;

    private boolean autoGenerated;

    private Operator user;

    private String text;

    private String json;

    // GETTER
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Unit getUnit() {
        return unit;
    }

    @JsonProperty("unit")
    public Integer getSlimUnit() {
      return unit != null ? unit.getId() : null;
    }

    public Incident getIncident() {
        return incident;
    }

    @JsonProperty("incident")
    public Integer getSlimIncident() {
      return incident != null ? incident.getId() : null;
    }

    public TaskState getState() {
        return state;
    }

    public boolean isAutoGenerated() {
        return autoGenerated;
    }

    public Operator getUser() {
        return user;
    }

    @JsonProperty("user")
    public Map<String, Object> getSlimUser() {
      if (user == null) {
        return null;
      }

      Map<String, Object> map = new HashMap<>();
      map.put("username", user.getUsername());
      return map;
    }

    public String getText() {
        return text;
    }

    @JsonIgnore
    public String getJson() {
        return json;
    }

    public JsonContainer getChanges() {
      if (json == null) {
        return null;
      }

      ObjectMapper mapper = new ObjectMapper();
      JsonContainer changes;
      try {
        changes = mapper.readValue(json, JsonContainer.class);
      } catch (IOException e) {
        Logger.getLogger(LogEntry.class).error(null, e);
        changes = null;
      }

      return changes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public Concern getConcern() {
        return concern;
    }

    public void setConcern(Concern concern) {
        this.concern = concern;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public void setAutoGenerated(boolean autoGenerated) {
        this.autoGenerated = autoGenerated;
    }

    public void setUser(Operator user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setChanges(JsonContainer changes) {
      if (changes == null) {
        this.json = null;
      } else {
        ObjectMapper mapper = new ObjectMapper();
        try {
          json = mapper.writeValueAsString(changes);
        } catch (IOException e) {
          Logger.getLogger(LogEntry.class).error(null, e);
          json = null;
        }
      }
    }

    public LogEntryType getType() {
        return type;
    }

    public void setType(LogEntryType type) {
        this.type = type;
    }
}
