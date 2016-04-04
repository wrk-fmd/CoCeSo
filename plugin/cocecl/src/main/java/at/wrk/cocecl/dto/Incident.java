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

    private Incident() {
        super();
    }

    private Incident(final int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public IncidentState getIncidentState() {
        return incidentState;
    }

    public void setIncidentState(final IncidentState incidentState) {
        this.incidentState = incidentState;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    public Boolean getBlue() {
        return blue;
    }

    public void setBlue(final boolean blue) {
        this.blue = blue;
    }

    public Collection<Unit> getUnits() {
        return units;
    }

    public void setUnits(final Collection<Unit> units) {
        this.units = units;
    }

    public String getCasusNr() {
        return casusNr;
    }

    public void setCasusNr(final String casusNr) {
        this.casusNr = casusNr;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(final String info) {
        this.info = info;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(final String caller) {
        this.caller = caller;
    }

    public Position getBo() {
        return bo;
    }

    public void setBo(final Position bo) {
        this.bo = bo;
    }

    public Position getAo() {
        return ao;
    }

    public void setAo(final Position ao) {
        this.ao = ao;
    }

    public IncidentType getType() {
        return type;
    }

    public void setType(final IncidentType type) {
        this.type = type;
    }

    public static Incident create(final int id) {
        return new Incident(id);
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
                "id=" + id + "," +
                "type=" + type + "," +
                "units=" + units + "," +
                "...TODO..." +
                "]";
    }
}
