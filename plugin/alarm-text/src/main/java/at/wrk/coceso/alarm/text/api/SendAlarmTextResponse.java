package at.wrk.coceso.alarm.text.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendAlarmTextResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String errorDescription;
    private final Integer error;

    public static SendAlarmTextResponse createError(final String errorDescription, final int error) {
        return new SendAlarmTextResponse(false, errorDescription, error);
    }

    public static SendAlarmTextResponse createSuccess() {
        return new SendAlarmTextResponse(true, null, null);
    }

    private SendAlarmTextResponse(final boolean success, final String errorDescription, final Integer error) {
        this.success = success;
        this.errorDescription = errorDescription;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public Integer getError() {
        return error;
    }

    @Override
    public String toString() {
        return "SendAlarmTextResponse{" +
                "success=" + success +
                ", errorDescription='" + errorDescription + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
