package at.wrk.coceso.dto.incident;

import at.wrk.coceso.dto.point.PointDto;
import at.wrk.coceso.dto.task.TaskDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class IncidentDto {

    private Long id;
    private Long concern;
    private IncidentClosedReasonDto closed;
    private boolean priority;
    private boolean blue;
    private Collection<TaskDto> units;
    private PointDto bo;
    private PointDto ao;
    private String casusNr;
    private String info;
    private String caller;
    private IncidentTypeDto type;
    private Long patient;
    private String section;

    @JsonFormat(shape = Shape.NUMBER)
    private Instant created;

    @JsonFormat(shape = Shape.NUMBER)
    private Instant arrival;

    @JsonFormat(shape = Shape.NUMBER)
    private Instant stateChange;

    @JsonFormat(shape = Shape.NUMBER)
    private Instant ended;
}
