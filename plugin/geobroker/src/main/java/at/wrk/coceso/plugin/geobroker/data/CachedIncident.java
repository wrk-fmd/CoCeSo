package at.wrk.coceso.plugin.geobroker.data;

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

    public CachedIncident(
            final GeoBrokerIncident incident,
            final List<String> assignedExternalUnitIds,
            final GeoBrokerPoint destination,
            final int concernId) {
        this.incident = Objects.requireNonNull(incident);
        this.assignedExternalUnitIds = assignedExternalUnitIds;
        this.destination = destination;
        this.concernId = concernId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, GeoBrokerToStringStyle.STYLE)
                .append("incident", incident)
                .append("assignedExternalUnitIds", assignedExternalUnitIds)
                .append("destination", destination)
                .append("concernId", concernId)
                .toString();
    }
}
