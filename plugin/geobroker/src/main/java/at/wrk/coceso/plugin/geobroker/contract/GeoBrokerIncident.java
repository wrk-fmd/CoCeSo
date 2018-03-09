package at.wrk.coceso.plugin.geobroker.contract;

import at.wrk.coceso.plugin.geobroker.GeoBrokerToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class GeoBrokerIncident implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String type;
    private final Boolean priority;
    private final Boolean blue;
    private final String info;
    private final GeoBrokerPoint location;

    public GeoBrokerIncident(
            final String id,
            final String type,
            final Boolean priority,
            final Boolean blue,
            final String info,
            final GeoBrokerPoint location) {
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.blue = blue;
        this.info = info;
        this.location = location;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, GeoBrokerToStringStyle.STYLE)
                .append("id", id)
                .append("type", type)
                .append("priority", priority)
                .append("blue", blue)
                .append("info", info)
                .append("location", location)
                .toString();
    }
}
