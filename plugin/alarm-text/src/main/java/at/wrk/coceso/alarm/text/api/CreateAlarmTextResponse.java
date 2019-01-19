package at.wrk.coceso.alarm.text.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAlarmTextResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String errorDescription;
    private final String alarmText;
    private final AlarmTextType type;

    public CreateAlarmTextResponse(final String errorDescription, final String alarmText, final AlarmTextType type) {
        this.success = errorDescription == null;
        this.errorDescription = errorDescription;
        this.alarmText = alarmText;
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getAlarmText() {
        return alarmText;
    }

    public AlarmTextType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CreateAlarmTextResponse{" +
                "success=" + success +
                ", errorDescription='" + errorDescription + '\'' +
                ", alarmText='" + alarmText + '\'' +
                ", type=" + type +
                '}';
    }
}
