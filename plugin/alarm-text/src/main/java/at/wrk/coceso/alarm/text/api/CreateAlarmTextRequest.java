package at.wrk.coceso.alarm.text.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CreateAlarmTextRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Integer incidentId;
    private final AlarmTextType type;

    @JsonCreator
    public CreateAlarmTextRequest(
            @JsonProperty("incidentId") final Integer incidentId,
            @JsonProperty("type") final AlarmTextType type) {
        this.incidentId = incidentId;
        this.type = type;
    }

    public Integer getIncidentId() {
        return incidentId;
    }

    public AlarmTextType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CreateAlarmTextRequest{" +
                "incidentId=" + incidentId +
                ", type=" + type +
                '}';
    }
}
