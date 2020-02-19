package at.wrk.coceso.plugin.geobroker.data;

import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.plugin.geobroker.GeoBrokerToStringStyle;
import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.contract.broker.GeoBrokerPoint;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class CachedIncident implements Serializable {
    private static final long serialVersionUID = 1L;

    private final GeoBrokerIncident incident;
    private final Map<String, TaskState> assignedExternalUnitIds;
    private final GeoBrokerPoint destination;
    private final int concernId;
    private final int incidentId;
    private final IncidentType incidentType;
    private final IncidentState incidentState;

    public CachedIncident(
            final GeoBrokerIncident incident,
            final Map<String, TaskState> assignedExternalUnitIds,
            final GeoBrokerPoint destination,
            final int concernId,
            final int incidentId, final IncidentType incidentType,
            final IncidentState incidentState) {
        this.incident = Objects.requireNonNull(incident);
        this.assignedExternalUnitIds = assignedExternalUnitIds;
        this.destination = destination;
        this.concernId = concernId;
        this.incidentId = incidentId;
        this.incidentType = incidentType;
        this.incidentState = incidentState;
    }

    public String getGeoBrokerIncidentId() {
        return incident.getId();
    }

    public GeoBrokerIncident getIncident() {
        return incident;
    }

    public Map<String, TaskState> getAssignedExternalUnitIds() {
        return assignedExternalUnitIds;
    }

    public GeoBrokerPoint getDestination() {
        return destination;
    }

    public int getConcernId() {
        return concernId;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public IncidentType getIncidentType() {
        return incidentType;
    }

    public IncidentState getIncidentState() {
        return incidentState;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CachedIncident that = (CachedIncident) o;
        return concernId == that.concernId &&
                Objects.equals(incident, that.incident) &&
                Objects.equals(assignedExternalUnitIds, that.assignedExternalUnitIds) &&
                Objects.equals(destination, that.destination) &&
                incidentId == that.incidentId &&
                incidentType == that.incidentType &&
                incidentState == that.incidentState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(incident, assignedExternalUnitIds, destination, concernId, incidentType, incidentState, incidentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, GeoBrokerToStringStyle.STYLE)
                .append("incident", incident)
                .append("assignedExternalUnitIds", assignedExternalUnitIds)
                .append("destination", destination)
                .append("concernId", concernId)
                .append("incidentId", incidentId)
                .append("incidentType", incidentType)
                .toString();
    }
}
