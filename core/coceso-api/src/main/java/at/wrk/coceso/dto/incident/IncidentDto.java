package at.wrk.coceso.dto.incident;

import at.wrk.coceso.dto.task.TaskDto;
import at.wrk.fmd.mls.geocoding.api.dto.PointDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class IncidentDto {

    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    private Long concern;

    @Schema(required = true, nullable = true)
    private IncidentClosedReasonDto closed;

    @Schema(required = true)
    private boolean priority;

    @Schema(required = true)
    private boolean blue;

    @Schema(required = true)
    private Collection<TaskDto> units;

    @Schema(required = true, nullable = true)
    private PointDto bo;

    @Schema(required = true, nullable = true)
    private PointDto ao;

    @Schema(required = true)
    private String casusNr;

    @Schema(required = true)
    private String info;

    @Schema(required = true)
    private String caller;

    @Schema(required = true)
    private IncidentTypeDto type;

    @Schema(required = true, nullable = true)
    private Long patient;

    @Schema(required = true, nullable = true)
    private String section;

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true)
    private Instant created;

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true, nullable = true)
    private Instant arrival;

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true, nullable = true)
    private Instant stateChange;

    @JsonFormat(shape = Shape.NUMBER)
    @Schema(required = true, nullable = true)
    private Instant ended;
}
