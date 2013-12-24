package at.wrk.coceso.entity;


import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitState;

import java.util.List;
import java.util.Map;

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

    /**
     * Default Values for Insert in Database
     */
    public void prepareNotNull(){
        if(state == null) state = UnitState.AD;
        if(call == null) call = "";
        if(ani == null) ani = "";
        if(info == null) info = "";
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
        return position;
    }

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
