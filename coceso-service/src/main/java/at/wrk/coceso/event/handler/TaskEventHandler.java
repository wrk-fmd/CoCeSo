package at.wrk.coceso.event.handler;

import at.wrk.coceso.event.events.IncidentEvent;
import at.wrk.coceso.event.events.TaskEvent;
import at.wrk.coceso.event.events.UnitEvent;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TaskEventHandler implements EventHandler<TaskEvent> {

    private final EventBus eventBus;

    @Autowired
    public TaskEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Class<TaskEvent> type() {
        return TaskEvent.class;
    }

    @Override
    public void handle(TaskEvent event) {
        // Notify for incident and unit
        eventBus.publish(new IncidentEvent(event.getIncident()));
        eventBus.publish(new UnitEvent(event.getUnit()));
    }
}
