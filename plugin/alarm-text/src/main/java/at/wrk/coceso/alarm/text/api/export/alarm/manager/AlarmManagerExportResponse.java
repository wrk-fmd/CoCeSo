package at.wrk.coceso.alarm.text.api.export.alarm.manager;

import at.wrk.coceso.contract.ToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class AlarmManagerExportResponse {
    private final List<AlarmManagerUnit> units;

    public AlarmManagerExportResponse(final List<AlarmManagerUnit> units) {
        this.units = units;
    }

    public List<AlarmManagerUnit> getUnits() {
        return units;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.STYLE)
                .append("units", units)
                .toString();
    }
}
