package at.wrk.coceso.event.events;

import at.wrk.fmd.mls.event.Event;
import lombok.Getter;

@Getter
public class ContainerDeletedEvent implements Event {

    private final Long concern;
    private final Long id;

    @SuppressWarnings("unused")
    private ContainerDeletedEvent() {
        // Required for event handler worker
        this.concern = null;
        this.id = null;
    }

    public ContainerDeletedEvent(Long concern, Long id) {
        this.concern = concern;
        this.id = id;
    }
}
