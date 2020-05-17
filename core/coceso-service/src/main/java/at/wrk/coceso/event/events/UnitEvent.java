package at.wrk.coceso.event.events;

import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.fmd.mls.event.Event;
import lombok.Getter;

@Getter
public class UnitEvent implements Event {

    private final UnitDto data;

    @SuppressWarnings("unused")
    private UnitEvent() {
        // Required for event handler worker
        this.data = null;
    }

    public UnitEvent(UnitDto data) {
        this.data = data;
    }
}
