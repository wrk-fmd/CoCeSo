package at.wrk.coceso.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class TaskDto {

    private Long incident;
    private Long unit;
    private TaskStateDto state;

    @JsonFormat(shape = Shape.NUMBER)
    private Instant updated;

    @JsonFormat(shape = Shape.NUMBER)
    private Instant alarmSent;

    @JsonFormat(shape = Shape.NUMBER)
    private Instant casusSent;

}
