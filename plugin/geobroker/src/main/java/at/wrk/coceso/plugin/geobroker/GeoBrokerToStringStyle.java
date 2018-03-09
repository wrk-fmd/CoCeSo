package at.wrk.coceso.plugin.geobroker;

import org.apache.commons.lang3.builder.ToStringStyle;

public class GeoBrokerToStringStyle extends ToStringStyle {
    public static final GeoBrokerToStringStyle STYLE = new GeoBrokerToStringStyle();

    private GeoBrokerToStringStyle() {
        setUseShortClassName(true);
        setUseIdentityHashCode(false);
    }
}
