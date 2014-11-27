package at.wrk.coceso.entity;


import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.helper.ChangePair;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class Unit {

    private int id;

    private Integer concern;

    private UnitState state;

    private String call;

    private String ani; // Radio ID

    private boolean withDoc;

    private boolean portable;

    private boolean transportVehicle;

    private List<Person> crew;

    private String info;

    private Point position;

    private Point home;

    private Map<Integer, TaskState> incidents;

    public Unit() {
    }

    public Unit(int id) {
      this.id = id;
    }

    /**
     * Default Values for Insert in Database
     */
    public void prepareNotNull(){
        if(state == null) state = UnitState.AD;
        if(call == null) call = "";
        if(ani == null) ani = "";
        if(info == null) info = "";
    }

    public Map<String, ChangePair<Object>> changes(Unit old) {
      Map<String, ChangePair<Object>> changes = new HashMap<>();

      if (old == null) {
        // Should not compare to empty unit, because no adding is done in main
        Logger.getLogger(Unit.class).warn("Unit.changes(): Tried to compare to null!");
        return null;
      }

      if (state != null && state != old.state) {
        changes.put("state", new ChangePair(old.state, state));
      }
      if (info != null && !info.equals(old.info)) {
        changes.put("info", new ChangePair(old.info, info));
      }
      if (position != null && !position.equals(old.position)) {
        changes.put("position", new ChangePair(old.position != null ? old.position.getInfo() : null, position.getInfo()));
      }

      return changes;
    }

    public Map<String, ChangePair<Object>> changesPosition(Unit old) {
      Map<String, ChangePair<Object>> changes = new HashMap<>();

      if (old == null) {
        // Should not compare to empty unit, because no adding is done in main
        Logger.getLogger(Unit.class).warn("Unit.changesPosition(): Tried to compare to null!");
        return null;
      }

      if (position != null && !position.equals(old.position)) {
        changes.put("position", new ChangePair(old.position != null ? old.position.getInfo() : null, position.getInfo()));
      }

      return changes;
    }

    public Map<String, ChangePair<Object>> changesFull(Unit old) {
      Map<String, ChangePair<Object>> changes = new HashMap<>();

      if (old == null) {
        if (call != null && !call.isEmpty()) {
          changes.put("call", new ChangePair(null, call));
        }
        if (ani != null && !ani.isEmpty()) {
          changes.put("ani", new ChangePair(null, ani));
        }
        if (info != null && !info.isEmpty()) {
          changes.put("info", new ChangePair(null, info));
        }
        if (!Point.isEmpty(home)) {
          changes.put("home", new ChangePair(null, home.getInfo()));
        }
        changes.put("withDoc", new ChangePair(null, withDoc));
        changes.put("portable", new ChangePair(null, portable));
        changes.put("transportVehicle", new ChangePair(null, transportVehicle));
      } else {
        if (call != null && !call.equals(old.call)) {
          changes.put("call", new ChangePair(old.call, call));
        }
        if (ani != null && !ani.equals(old.ani)) {
          changes.put("ani", new ChangePair(old.ani, ani));
        }
        if (withDoc != old.withDoc) {
          changes.put("withDoc", new ChangePair(old.withDoc, withDoc));
        }
        if (portable != old.portable) {
          changes.put("portable", new ChangePair(old.portable, portable));
        }
        if (transportVehicle != old.transportVehicle) {
          changes.put("transportVehicle", new ChangePair(old.transportVehicle, transportVehicle));
        }
        if (info != null && !info.equals(old.info)) {
          changes.put("info", new ChangePair(old.info, info));
        }
        if (home != null && !home.equals(old.home)) {
          changes.put("home", new ChangePair(old.home != null ? old.home.getInfo() : null, home.getInfo()));
        }
      }

      return changes;
    }

    public int getId() {
        return id;
    }

    public String getCall() {
        return call;
    }

    public String getAni() {
        return ani;
    }

    public boolean isWithDoc() {
        return withDoc;
    }

    public boolean isPortable() {
        return portable;
    }

    public boolean isTransportVehicle() {
        return transportVehicle;
    }

    public String getInfo() {
        return info;
    }

    public Unit slimCopy() {
        Unit ret = new Unit();
        ret.id = this.id;
        ret.concern = this.concern;
        return ret;
    }

    public UnitState getState() {
        return state;
    }

    public Point getPosition() {
        // Temporary Bugfix -- REMOVED
        return position; // == null ? dummyPoint() : position;
    }

    /*private Point dummyPoint() {
        Point ret = new Point();
        ret.setId(-1);
        ret.setInfo("");
        return ret;
    } */

    public Map<Integer, TaskState> getIncidents() {
        return incidents;
    }

    public Point getHome() {
        return home;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getConcern() {
        return concern;
    }

    public void setConcern(Integer concern) {
        this.concern = concern;
    }

    public void setState(UnitState state) {
        this.state = state;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public void setAni(String ani) {
        this.ani = ani;
    }

    public void setWithDoc(boolean withDoc) {
        this.withDoc = withDoc;
    }

    public void setPortable(boolean portable) {
        this.portable = portable;
    }

    public void setTransportVehicle(boolean transportVehicle) {
        this.transportVehicle = transportVehicle;
    }

    public List<Person> getCrew() {
        return crew;
    }

    public void setCrew(List<Person> crew) {
        this.crew = crew;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setHome(Point home) {
        this.home = home;
    }

    public void setIncidents(Map<Integer, TaskState> incidents) {
        this.incidents = incidents;
    }
}
