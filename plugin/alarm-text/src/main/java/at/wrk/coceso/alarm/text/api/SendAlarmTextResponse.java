package at.wrk.coceso.alarm.text.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendAlarmTextResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String errorDescription;

    public SendAlarmTextResponse() {
        this(null);
    }

    public SendAlarmTextResponse(final String errorDescription) {
        this.success = errorDescription == null;
        this.errorDescription = errorDescription;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    @Override
    public String toString() {
        return "SendAlarmTextResponse{" +
                "success=" + success +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }
}
