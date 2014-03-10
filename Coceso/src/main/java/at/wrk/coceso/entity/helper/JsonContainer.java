package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;

public class JsonContainer {
    private Unit unit;
    private Incident incident;

    public JsonContainer() {
        super();
    }

    public JsonContainer(Unit unit, Incident incident) {
        this.unit = unit;
        this.incident = incident;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }
}
