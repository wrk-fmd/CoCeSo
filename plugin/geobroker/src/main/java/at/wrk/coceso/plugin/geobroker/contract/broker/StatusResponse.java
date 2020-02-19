package at.wrk.coceso.plugin.geobroker.contract.broker;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Objects;

public class StatusResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String instanceId;

    public StatusResponse(final String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusResponse that = (StatusResponse) o;
        return Objects.equals(instanceId, that.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("instanceId", instanceId)
                .toString();
    }
}
