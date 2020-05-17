package at.wrk.coceso.event.events;

import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.fmd.mls.event.Event;
import lombok.Getter;

@Getter
public class IncidentEvent implements Event {

    private final IncidentDto data;

    @SuppressWarnings("unused")
    protected IncidentEvent() {
        // Required for event handler worker
        this.data = null;
    }

    public IncidentEvent(IncidentDto data) {
        this.data = data;
    }
}
