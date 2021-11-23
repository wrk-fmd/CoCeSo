package at.wrk.coceso.event.events;

import at.wrk.coceso.dto.concern.ConcernDto;
import at.wrk.fmd.mls.event.Event;
import lombok.Getter;

@Getter
public class ConcernEvent implements Event {

    private final ConcernDto data;

    @SuppressWarnings("unused")
    private ConcernEvent() {
        // Required for event handler worker
        this.data = null;
    }

    public ConcernEvent(ConcernDto data) {
        this.data = data;
    }
}
