package at.wrk.coceso.plugin.geobroker.data;

import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.plugin.geobroker.GeoBrokerToStringStyle;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerIncident;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerPoint;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class CachedIncident implements Serializable {
    private static final long serialVersionUID = 1L;

    private final GeoBrokerIncident incident;
    private final List<String> assignedExternalUnitIds;
    private final GeoBrokerPoint destination;
    private final int concernId;
    private final IncidentType incidentType;
    private final IncidentState incidentState;

    public CachedIncident(
            final GeoBrokerIncident incident,
            final List<String> assignedExternalUnitIds,
            final GeoBrokerPoint destination,
            final int concernId,
            final IncidentType incidentType,
            final IncidentState incidentState) {
        this.incident = Objects.requireNonNull(incident);
        this.assignedExternalUnitIds = assignedExternalUnitIds;
        this.destination = destination;
        this.concernId = concernId;
        this.incidentType = incidentType;
        this.incidentState = incidentState;
    }

    public String getId() {
        return incident.getId();
    }

    public GeoBrokerIncident getIncident() {
        return incident;
    }

    public List<String> getAssignedExternalUnitIds() {
        return assignedExternalUnitIds;
    }

    public GeoBrokerPoint getDestination() {
        return destination;
    }

    public int getConcernId() {
        return concernId;
    }

    public IncidentType getIncidentType() {
        return incidentType;
    }

    public IncidentState getIncidentState() {
        return incidentState;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, GeoBrokerToStringStyle.STYLE)
                .append("incident", incident)
                .append("assignedExternalUnitIds", assignedExternalUnitIds)
                .append("destination", destination)
                .append("concernId", concernId)
                .append("incidentType", incidentType)
                .toString();
    }
}
