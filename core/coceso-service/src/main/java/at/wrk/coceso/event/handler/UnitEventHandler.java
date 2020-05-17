package at.wrk.coceso.event.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.coceso.event.events.UnitEvent;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import at.wrk.fmd.mls.replay.message.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class UnitEventHandler implements EventHandler<UnitEvent> {

    private final EventBus eventBus;

    @Autowired
    public UnitEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Class<UnitEvent> type() {
        return UnitEvent.class;
    }

    @Override
    public void handle(UnitEvent event) {
        UnitDto data = event.getData();

        // Notify for STOMP
        eventBus.publish(new NotificationMessage(CocesoExchangeNames.STOMP_UNITS, data.getConcern(), data));
    }
}
