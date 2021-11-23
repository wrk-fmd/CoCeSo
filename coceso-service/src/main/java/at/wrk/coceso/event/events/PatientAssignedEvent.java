package at.wrk.coceso.event.events;

import at.wrk.coceso.dto.incident.IncidentDto;

public class PatientAssignedEvent extends IncidentEvent {

    @SuppressWarnings("unused")
    private PatientAssignedEvent() {
        // Required for event handler worker
    }

    public PatientAssignedEvent(IncidentDto data) {
        super(data);
    }
}
