package at.wrk.coceso.config;

import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EventBusConfiguration {

    @Autowired
    public void configureEventHandlers(EventBus eventBus, List<EventHandler<?>> handlers) {
        handlers.forEach(eventBus::registerHandler);
    }
}
