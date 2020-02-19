package at.wrk.coceso.plugin.geobroker.action;

import at.wrk.coceso.entity.enums.TaskState;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;
import java.util.UUID;

public class NextStateUnitAction implements UnitAction {
    private final int unitId;
    private final int incidentId;
    private final String geoBrokerIncidentId;
    private final TaskState plannedState;

    public NextStateUnitAction(
            final int unitId,
            final int incidentId,
            final String geoBrokerIncidentId,
            final TaskState plannedState) {
        this.unitId = unitId;
        this.incidentId = incidentId;
        this.geoBrokerIncidentId = geoBrokerIncidentId;
        this.plannedState = plannedState;
    }

    int getUnitId() {
        return unitId;
    }

    int getIncidentId() {
        return incidentId;
    }

    String getGeoBrokerIncidentId() {
        return geoBrokerIncidentId;
    }

    TaskState getPlannedState() {
        return plannedState;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NextStateUnitAction that = (NextStateUnitAction) o;
        return unitId == that.unitId &&
                incidentId == that.incidentId &&
                Objects.equals(geoBrokerIncidentId, that.geoBrokerIncidentId) &&
                plannedState == that.plannedState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitId, incidentId, geoBrokerIncidentId, plannedState);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("unitId", unitId)
                .append("incidentId", incidentId)
                .append("geoBrokerIncidentId", geoBrokerIncidentId)
                .append("plannedState", plannedState)
                .toString();
    }

    @Override
    public ExecutableAction buildExecutableAction() {
        return new NextStateExecutableAction(UUID.randomUUID(), this);
    }
}
