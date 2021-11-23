package at.wrk.coceso.event.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.container.ContainerDto;
import at.wrk.coceso.event.events.ContainerEvent;
import at.wrk.fmd.mls.amqp.event.NotificationMessage;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ContainerEventHandler implements EventHandler<ContainerEvent> {

    private final EventBus eventBus;

    @Autowired
    public ContainerEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Class<ContainerEvent> type() {
        return ContainerEvent.class;
    }

    @Override
    public void handle(ContainerEvent event) {
        ContainerDto data = event.getData();

        // Notify for STOMP
        eventBus.publish(new NotificationMessage(CocesoExchangeNames.STOMP_CONTAINERS, data.getConcern(), data));
    }
}
