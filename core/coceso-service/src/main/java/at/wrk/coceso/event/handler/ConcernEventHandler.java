package at.wrk.coceso.event.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.concern.ConcernDto;
import at.wrk.coceso.event.events.ConcernEvent;
import at.wrk.fmd.mls.amqp.event.NotificationMessage;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ConcernEventHandler implements EventHandler<ConcernEvent> {

    private final EventBus eventBus;

    @Autowired
    public ConcernEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Class<ConcernEvent> type() {
        return ConcernEvent.class;
    }

    @Override
    public void handle(ConcernEvent event) {
        ConcernDto data = event.getData();

        // Notify for STOMP
        eventBus.publish(new NotificationMessage(CocesoExchangeNames.STOMP_CONCERNS, null, data));
    }
}
