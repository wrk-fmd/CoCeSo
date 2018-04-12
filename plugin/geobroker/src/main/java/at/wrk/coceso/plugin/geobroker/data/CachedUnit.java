package at.wrk.coceso.plugin.geobroker.data;

import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitType;
import at.wrk.coceso.plugin.geobroker.GeoBrokerToStringStyle;
import at.wrk.coceso.plugin.geobroker.contract.GeoBrokerUnit;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class CachedUnit implements Serializable {
    private static final long serialVersionUID = 1L;

    private final GeoBrokerUnit unit;
    private final Map<String, TaskState> incidentsWithState;
    private final UnitType unitType;
    private final int concernId;

    public CachedUnit(
            final GeoBrokerUnit unit,
            final Map<String, TaskState> incidentsWithState,
            final UnitType unitType,
            final int concernId) {
        this.unit = Objects.requireNonNull(unit);
        this.incidentsWithState = incidentsWithState;
        this.unitType = unitType;
        this.concernId = concernId;
    }

    public String getId() {
        return unit.getId();
    }

    public GeoBrokerUnit getUnit() {
        return unit;
    }

    public Map<String, TaskState> getIncidentsWithState() {
        return incidentsWithState;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public int getConcernId() {
        return concernId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CachedUnit that = (CachedUnit) o;
        return concernId == that.concernId &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(incidentsWithState, that.incidentsWithState) &&
                unitType == that.unitType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit, incidentsWithState, unitType, concernId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, GeoBrokerToStringStyle.STYLE)
                .append("unit", unit)
                .append("incidentsWithState", incidentsWithState)
                .append("unitType", unitType)
                .append("concernId", concernId)
                .toString();
    }
}
