package at.wrk.coceso.entity;


import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.ChangePair;
import java.util.HashMap;

import java.util.Map;
import org.apache.log4j.Logger;

public class Incident {

    private int id;

    private Integer concern;

    private IncidentState state;

    private Integer priority;

    private Boolean blue;

    private Map<Integer, TaskState> units;

    private Point bo;

    private Point ao;

    private String casusNr;

    private String info;

    private String caller;

    private IncidentType type;

    public Incident() {
    }

    public Incident(int id) {
      this.id = id;
    }

    public Incident slimCopy() {
        Incident ret = new Incident();
        ret.setId(this.id);
        ret.setConcern(this.concern);
        ret.setType(this.type);

        return ret;
    }

    public Map<String, ChangePair<Object>> changes(Incident old) {
      Map<String, ChangePair<Object>> changes = new HashMap<>();

      if (old == null) {
        if (state != null) {
          changes.put("state", new ChangePair(null, state));
        }
        if (priority != null) {
          changes.put("priority", new ChangePair(null, priority));
        }
        if (blue != null) {
          changes.put("blue", new ChangePair(null, blue));
        }
        if (!Point.isEmpty(bo)) {
          changes.put("bo", new ChangePair(null, bo.getInfo()));
        }
        if (!Point.isEmpty(ao)) {
          changes.put("ao", new ChangePair(null, ao.getInfo()));
        }
        if (casusNr != null && !casusNr.isEmpty()) {
          changes.put("casusNr", new ChangePair(null, casusNr));
        }
        if (info != null && !info.isEmpty()) {
          changes.put("info", new ChangePair(null, info));
        }
        if (caller != null && !caller.isEmpty()) {
          changes.put("caller", new ChangePair(null, caller));
        }
        if (type != null) {
          changes.put("type", new ChangePair(null, type));
        }
      } else {
        if (state != null && state != old.state) {
          changes.put("state", new ChangePair(old.state, state));
        }
        if (priority != null && !priority.equals(old.priority)) {
          changes.put("priority", new ChangePair(old.priority, priority));
        }
        if (blue != null && !blue.equals(old.blue)) {
          changes.put("blue", new ChangePair(old.blue, blue));
        }
        if (bo != null && !bo.equals(old.bo) && (old.bo != null || !Point.isEmpty(bo))) {
          changes.put("bo", new ChangePair(old.bo != null ? old.bo.getInfo() : null, bo.getInfo()));
        }
        if (ao != null && !ao.equals(old.ao) && (old.ao != null || !Point.isEmpty(ao))) {
          changes.put("ao", new ChangePair(old.ao != null ? old.ao.getInfo() : null, ao.getInfo()));
        }
        if (casusNr != null && !casusNr.equals(old.casusNr)) {
          changes.put("casusNr", new ChangePair(old.casusNr, casusNr));
        }
        if (info != null && !info.equals(old.info)) {
          changes.put("info", new ChangePair(old.info, info));
        }
        if (caller != null && !caller.equals(old.caller)) {
          changes.put("caller", new ChangePair(old.caller, caller));
        }
        if (type != null && type != old.type) {
          changes.put("type", new ChangePair(old.type, type));
        }
      }

      return changes;
    }

    public Map<String, ChangePair<Object>> changesState(Incident old) {
      Map<String, ChangePair<Object>> changes = new HashMap<>();

      if (old == null) {
        // Should not compare to empty unit, because no state auto set occurs on new incidents
        Logger.getLogger(Incident.class).warn("Incident.changesState(): Tried to compare to null!");
        return null;
      }

      if (state != null && !state.equals(old.state)) {
        changes.put("state", new ChangePair(old.state, state));
      }

      return changes;
    }

    public int getId() {
        return id;
    }

    public IncidentState getState() {
        return state;
    }

    public Integer getPriority() {
        return priority;
    }

    public Boolean getBlue() {
        return blue;
    }

    public Map<Integer, TaskState> getUnits() {
        return units;
    }

    public Point getBo() {
        return bo;
    }

    public Point getAo() {
        return ao;
    }

    public String getCasusNr() {
        return casusNr;
    }

    public String getInfo() {
        return info;
    }

    public String getCaller() {
        return caller;
    }

    public IncidentType getType() {
        return type;
    }

    public Integer getConcern() {
        return concern;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setConcern(Integer concern) {
        this.concern = concern;
    }

    public void setState(IncidentState state) {
        this.state = state;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setBlue(Boolean blue) {
        this.blue = blue;
    }

    public void setUnits(Map<Integer, TaskState> units) {
        this.units = units;
    }

    public void setBo(Point bo) {
        this.bo = bo;
    }

    public void setAo(Point ao) {
        this.ao = ao;
    }

    public void setCasusNr(String casusNr) {
        this.casusNr = casusNr;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }
}
