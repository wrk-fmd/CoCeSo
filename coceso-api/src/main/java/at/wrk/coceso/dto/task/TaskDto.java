package at.wrk.coceso.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class TaskDto {

    @Schema(required = true)
    private Long incident;

    @Schema(required = true)
    private Long unit;

    @Schema(required = true)
    private TaskStateDto state;

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true)
    private Instant updated;

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true, nullable = true)
    private Instant alarmSent;

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true, nullable = true)
    private Instant casusSent;
}
