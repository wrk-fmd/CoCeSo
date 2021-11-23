package at.wrk.coceso.event.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.patient.PatientDto;
import at.wrk.coceso.event.events.PatientEvent;
import at.wrk.fmd.mls.amqp.event.NotificationMessage;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class PatientEventHandler implements EventHandler<PatientEvent> {

    private final EventBus eventBus;

    @Autowired
    public PatientEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Class<PatientEvent> type() {
        return PatientEvent.class;
    }

    @Override
    public void handle(PatientEvent event) {
        PatientDto data = event.getData();

        // Notify for STOMP
        eventBus.publish(new NotificationMessage(CocesoExchangeNames.STOMP_PATIENTS, data.getConcern(), data));
    }
}
