package at.wrk.coceso.event.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.event.events.ContainerDeletedEvent;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import at.wrk.fmd.mls.replay.dto.DeletionDto;
import at.wrk.fmd.mls.replay.message.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ContainerDeletedEventHandler implements EventHandler<ContainerDeletedEvent> {

    private final EventBus eventBus;

    @Autowired
    public ContainerDeletedEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Class<ContainerDeletedEvent> type() {
        return ContainerDeletedEvent.class;
    }

    @Override
    public void handle(ContainerDeletedEvent event) {
        DeletionDto data = new DeletionDto(event.getId());

        // Notify for STOMP
        eventBus.publish(new NotificationMessage(CocesoExchangeNames.STOMP_CONTAINERS, event.getConcern(), data));
    }
}
