package at.wrk.coceso.plugin.geobroker.contract.qr;

import at.wrk.coceso.entity.enums.UnitType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class ExternalUnit implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int unitId;
    private final UnitType type;
    private final String name;
    private final String externalUnitId;
    private final String token;

    public ExternalUnit(final int unitId, final UnitType type, final String name, final String externalUnitId, final String token) {
        this.unitId = unitId;
        this.type = type;
        this.name = name;
        this.externalUnitId = externalUnitId;
        this.token = token;
    }

    public int getUnitId() {
        return unitId;
    }

    public UnitType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getExternalUnitId() {
        return externalUnitId;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExternalUnit that = (ExternalUnit) o;
        return unitId == that.unitId &&
                type == that.type &&
                Objects.equals(name, that.name) &&
                Objects.equals(externalUnitId, that.externalUnitId) &&
                Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitId, type, name, externalUnitId, token);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("unitId", unitId)
                .append("type", type)
                .append("name", name)
                .append("externalUnitId", externalUnitId)
                .append("token", token)
                .toString();
    }
}
