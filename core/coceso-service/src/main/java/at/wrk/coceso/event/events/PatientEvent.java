package at.wrk.coceso.event.events;

import at.wrk.coceso.dto.patient.PatientDto;
import at.wrk.fmd.mls.event.Event;
import lombok.Getter;

@Getter
public class PatientEvent implements Event {

    private final PatientDto data;

    @SuppressWarnings("unused")
    private PatientEvent() {
        // Required for event handler worker
        this.data = null;
    }

    public PatientEvent(PatientDto data) {
        this.data = data;
    }
}
