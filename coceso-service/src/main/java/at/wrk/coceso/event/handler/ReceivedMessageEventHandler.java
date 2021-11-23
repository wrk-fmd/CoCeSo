package at.wrk.coceso.event.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.message.ReceivedMessageDto;
import at.wrk.coceso.event.events.ReceivedMessageEvent;
import at.wrk.fmd.mls.amqp.event.NotificationMessage;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ReceivedMessageEventHandler implements EventHandler<ReceivedMessageEvent> {

    private final EventBus eventBus;

    @Autowired
    public ReceivedMessageEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Class<ReceivedMessageEvent> type() {
        return ReceivedMessageEvent.class;
    }

    @Override
    public void handle(ReceivedMessageEvent event) {
        ReceivedMessageDto data = event.getData();

        // Notify for STOMP
        eventBus.publish(new NotificationMessage(CocesoExchangeNames.STOMP_MESSAGES, null, data));
    }
}
