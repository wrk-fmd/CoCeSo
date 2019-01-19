package at.wrk.coceso.alarm.text.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SendAlarmTextRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Integer incidentId;
    private final String alarmText;
    private final AlarmTextType type;

    @JsonCreator
    public SendAlarmTextRequest(
            @JsonProperty("incidentId") final Integer incidentId,
            @JsonProperty("alarmText") final String alarmText,
            @JsonProperty("type") final AlarmTextType type) {
        this.incidentId = incidentId;
        this.alarmText = alarmText;
        this.type = type;
    }

    public Integer getIncidentId() {
        return incidentId;
    }

    public String getAlarmText() {
        return alarmText;
    }

    public AlarmTextType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SendAlarmTextRequest{" +
                "incidentId=" + incidentId +
                ", alarmText='" + alarmText + '\'' +
                ", type=" + type +
                '}';
    }
}
