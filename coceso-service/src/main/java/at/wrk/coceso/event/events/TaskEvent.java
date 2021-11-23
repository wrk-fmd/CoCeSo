package at.wrk.coceso.event.events;

import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.dto.task.TaskStateDto;
import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.fmd.mls.event.Event;
import lombok.Getter;

@Getter
public class TaskEvent implements Event {

    private final IncidentDto incident;
    private final UnitDto unit;
    private final TaskStateDto state;

    @SuppressWarnings("unused")
    protected TaskEvent() {
        // Required for event handler worker
        this.incident = null;
        this.unit = null;
        this.state = null;
    }

    public TaskEvent(IncidentDto incident, UnitDto unit, TaskStateDto state) {
        this.incident = incident;
        this.unit = unit;
        this.state = state;
    }
}
