package at.wrk.cocecl.dto;

import java.io.Serializable;
import java.util.Collection;

public class Incident implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private IncidentState incidentState;
    private Integer priority;
    private Boolean blue;
    private Collection<Unit> units;
    private String casusNr;
    private String info;
    private String caller;
    private Position bo;
    private Position ao;
    private IncidentType type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IncidentState getIncidentState() {
        return incidentState;
    }

    public void setIncidentState(IncidentState incidentState) {
        this.incidentState = incidentState;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getBlue() {
        return blue;
    }

    public void setBlue(Boolean blue) {
        this.blue = blue;
    }

    public Collection<Unit> getUnits() {
        return units;
    }

    public void setUnits(Collection<Unit> units) {
        this.units = units;
    }

    public String getCasusNr() {
        return casusNr;
    }

    public void setCasusNr(String casusNr) {
        this.casusNr = casusNr;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public Position getBo() {
        return bo;
    }

    public void setBo(Position bo) {
        this.bo = bo;
    }

    public Position getAo() {
        return ao;
    }

    public void setAo(Position ao) {
        this.ao = ao;
    }

    public IncidentType getType() {
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }
}
