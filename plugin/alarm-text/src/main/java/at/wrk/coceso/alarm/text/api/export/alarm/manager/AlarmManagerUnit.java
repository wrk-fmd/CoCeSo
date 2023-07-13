package at.wrk.coceso.alarm.text.api.export.alarm.manager;

import at.wrk.coceso.contract.ToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class AlarmManagerUnit {
    private final String id;
    private final String call;
    private final List<AnalogRadioContact> analogRadioIds;
    private final List<SmsContact> smsContacts;
    private final List<TetraContact> tetraContacts;

    public AlarmManagerUnit(
            final String id,
            final String call,
            final List<AnalogRadioContact> analogRadioIds,
            final List<SmsContact> smsContacts,
            final List<TetraContact> tetraContacts) {
        this.id = id;
        this.call = call;
        this.analogRadioIds = copyListOrEmpty(analogRadioIds);
        this.smsContacts = copyListOrEmpty(smsContacts);
        this.tetraContacts = copyListOrEmpty(tetraContacts);
    }

    private static <T> List<T> copyListOrEmpty(final List<T> nullableList) {
        return nullableList == null ? List.of() : List.copyOf(nullableList);
    }

    public String getId() {
        return id;
    }

    public String getCall() {
        return call;
    }

    public List<AnalogRadioContact> getAnalogRadioIds() {
        return analogRadioIds;
    }

    public List<SmsContact> getSmsContacts() {
        return smsContacts;
    }

    public List<TetraContact> getTetraContacts() {
        return tetraContacts;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.STYLE)
                .append("id", id)
                .append("call", call)
                .append("analogRadioIds", analogRadioIds)
                .append("smsContacts", smsContacts)
                .append("tetraContacts", tetraContacts)
                .toString();
    }
}
