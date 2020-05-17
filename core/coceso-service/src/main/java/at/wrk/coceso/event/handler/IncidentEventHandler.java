package at.wrk.coceso.event.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.event.events.IncidentEvent;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import at.wrk.fmd.mls.replay.message.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class IncidentEventHandler implements EventHandler<IncidentEvent> {

    private final EventBus eventBus;

    @Autowired
    public IncidentEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Class<IncidentEvent> type() {
        return IncidentEvent.class;
    }

    @Override
    public void handle(IncidentEvent event) {
        IncidentDto data = event.getData();

        // Notify for STOMP
        eventBus.publish(new NotificationMessage(CocesoExchangeNames.STOMP_INCIDENTS, data.getConcern(), data));
    }
}
