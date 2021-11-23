package at.wrk.coceso.event.events;

import at.wrk.coceso.dto.message.ReceivedMessageDto;
import at.wrk.fmd.mls.event.Event;
import lombok.Getter;

@Getter
public class ReceivedMessageEvent implements Event {

    private final ReceivedMessageDto data;

    @SuppressWarnings("unused")
    private ReceivedMessageEvent() {
        // Required for event handler worker
        this.data = null;
    }

    public ReceivedMessageEvent(ReceivedMessageDto data) {
        this.data = data;
    }
}
