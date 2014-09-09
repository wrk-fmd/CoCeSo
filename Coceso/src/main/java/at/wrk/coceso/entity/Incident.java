package at.wrk.coceso.entity;


import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;

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

    public TaskState nextState(int unit_id) {
        if(getState() == null || getType() == null || !getUnits().containsKey(unit_id))
            return null;

        List<TaskState> l = getType().getPossibleStates();
        if(l == null)
            return null;
        int index = l.indexOf(getUnits().get(unit_id));
        if(index < 0 || index > l.size() - 2)
            return null;

        return l.get(index+1);
    }

    public Incident slimCopy() {
        Incident ret = new Incident();
        ret.setId(this.id);
        ret.setConcern(this.concern);
        ret.setType(this.type);

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
