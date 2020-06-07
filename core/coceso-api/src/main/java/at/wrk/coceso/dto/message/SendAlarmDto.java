package at.wrk.coceso.dto.message;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
public class SendAlarmDto extends SendMessageDto {

    /**
     * Used to specify the recipients of the alarm message:
     * - ALL: send to all units, even if those that have been alerted before
     * - UNSENT: send to all units that have not been alerted before
     * - LIST: send only to specific units ({@link #units} is obligatory in this case)
     */
    @NotNull
    private AlarmRecipientsDto recipients;
    private Set<Long> units;

    /**
     * The type of message (ALARM or CASUS)
     */
    @NotNull
    private AlarmTypeDto type;

    public enum AlarmRecipientsDto {
        ALL, UNSENT, LIST
    }

    public enum AlarmTypeDto {
        ALARM, CASUS
    }
}
