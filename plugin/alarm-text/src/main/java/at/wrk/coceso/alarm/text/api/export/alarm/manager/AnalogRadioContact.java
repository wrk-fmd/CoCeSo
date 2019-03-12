package at.wrk.coceso.alarm.text.api.export.alarm.manager;

import at.wrk.coceso.contract.ToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AnalogRadioContact {
    private final String id;

    public AnalogRadioContact(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.STYLE)
                .append("id", id)
                .toString();
    }
}
