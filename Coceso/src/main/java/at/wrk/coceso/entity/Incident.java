package at.wrk.coceso.entity;


import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Incident {

    //private static HashMap<IncidentType, List<TaskState>> possibleStates;

    /*static {
        possibleStates = new HashMap<IncidentType, List<TaskState>>();
        List<TaskState> tmp = new ArrayList<TaskState>();

        tmp.add(TaskState.Assigned);
        tmp.add(TaskState.AAO);
        tmp.add(TaskState.Detached);

        possibleStates.put(IncidentType.HoldPosition, new ArrayList<TaskState>(tmp));
        possibleStates.put(IncidentType.Standby, new ArrayList<TaskState>(tmp));

        tmp.add(1, TaskState.ZAO);

        possibleStates.put(IncidentType.Relocation, new ArrayList<TaskState>(tmp));
        possibleStates.put(IncidentType.ToHome, new ArrayList<TaskState>(tmp));

        tmp.add(1, TaskState.ZBO);
        tmp.add(2, TaskState.ABO);

        possibleStates.put(IncidentType.Task, new ArrayList<TaskState>(tmp));
    }*/

    public int id;

    public Integer concern;

    public IncidentState state;

    public Integer priority;

    public Boolean blue;

    public Map<Integer, TaskState> units;

    public Point bo;

    public Point ao;

    public String casusNr;

    public String info;

    public String caller;

    public IncidentType type;

    public TaskState nextState(int unit_id) {
        if(state == null || type == null || !units.containsKey(unit_id))
            return null;

        List<TaskState> l = type.getPossibleStates();
        if(l == null)
            return null;
        int index = l.indexOf(units.get(unit_id));
        if(index < 0 || index > l.size() - 2)
            return null;

        TaskState ret = l.get(index+1);

        return ret;
    }

    public Incident slimCopy() {
        Incident ret = new Incident();
        ret.id = this.id;
        ret.concern = this.concern;
        ret.type = this.type;

        return ret;
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
