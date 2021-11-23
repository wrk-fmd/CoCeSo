package at.wrk.coceso.event.events;

import at.wrk.coceso.dto.container.ContainerDto;
import at.wrk.fmd.mls.event.Event;
import lombok.Getter;

@Getter
public class ContainerEvent implements Event {

    private final ContainerDto data;

    @SuppressWarnings("unused")
    private ContainerEvent() {
        // Required for event handler worker
        this.data = null;
    }

    public ContainerEvent(ContainerDto data) {
        this.data = data;
    }
}
