package at.wrk.coceso.alarm.text.api;

import at.wrk.coceso.contract.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAlarmTextResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final Integer error;
    private final String errorDescription;
    private final String alarmText;
    private final AlarmTextType type;

    public static CreateAlarmTextResponse createSuccess(final String alarmText, final AlarmTextType type) {
        return new CreateAlarmTextResponse(true, null, null, alarmText, type);
    }

    public static CreateAlarmTextResponse createError(final String errorDescription, final int error) {
        return new CreateAlarmTextResponse(false, error, errorDescription, null, null);
    }

    private CreateAlarmTextResponse(
            final boolean success,
            final Integer error,
            final String errorDescription,
            final String alarmText,
            final AlarmTextType type) {
        this.success = success;
        this.error = error;
        this.errorDescription = errorDescription;
        this.alarmText = alarmText;
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
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
        return new ToStringBuilder(this, ToStringStyle.STYLE)
                .append("success", success)
                .append("error", error)
                .append("errorDescription", errorDescription)
                .append("alarmText", alarmText)
                .append("type", type)
                .toString();
    }
}
