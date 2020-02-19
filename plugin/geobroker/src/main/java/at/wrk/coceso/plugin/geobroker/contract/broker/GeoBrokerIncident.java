package at.wrk.coceso.plugin.geobroker.contract.broker;

import at.wrk.coceso.plugin.geobroker.GeoBrokerToStringStyle;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class GeoBrokerIncident implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String type;
    private final Boolean priority;
    private final Boolean blue;
    private final String info;
    private final GeoBrokerPoint location;
    private final GeoBrokerPoint destination;
    private final Map<String, String> assignedUnits;

    public GeoBrokerIncident(
            final String id,
            final String type,
            final Boolean priority,
            final Boolean blue,
            final String info,
            final GeoBrokerPoint location,
            final GeoBrokerPoint destination,
            final Map<String, String> assignedUnits) {
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.blue = blue;
        this.info = info;
        this.location = location;
        this.destination = destination;
        this.assignedUnits = assignedUnits == null ? ImmutableMap.of() : ImmutableMap.copyOf(assignedUnits);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Boolean getPriority() {
        return priority;
    }

    public Boolean getBlue() {
        return blue;
    }

    public String getInfo() {
        return info;
    }

    public GeoBrokerPoint getLocation() {
        return location;
    }

    public GeoBrokerPoint getDestination() {
        return destination;
    }

    public Map<String, String> getAssignedUnits() {
        return assignedUnits;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoBrokerIncident that = (GeoBrokerIncident) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(type, that.type) &&
                Objects.equals(priority, that.priority) &&
                Objects.equals(blue, that.blue) &&
                Objects.equals(info, that.info) &&
                Objects.equals(location, that.location) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(assignedUnits, that.assignedUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, priority, blue, info, location, destination, assignedUnits);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, GeoBrokerToStringStyle.STYLE)
                .append("id", id)
                .append("type", type)
                .append("priority", priority)
                .append("blue", blue)
                .append("info", info)
                .append("location", location)
                .append("destination", destination)
                .append("assignedUnits", assignedUnits)
                .toString();
    }
}
