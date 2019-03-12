package at.wrk.coceso.alarm.text.api.export.alarm.manager;

import at.wrk.coceso.contract.ToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SmsContact {
    private final String owner;
    private final String phoneNumber;

    public SmsContact(final String owner, final String phoneNumber) {
        this.owner = owner;
        this.phoneNumber = phoneNumber;
    }

    public String getOwner() {
        return owner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.STYLE)
                .append("owner", owner)
                .append("phoneNumber", phoneNumber)
                .toString();
    }
}
