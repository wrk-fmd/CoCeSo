package at.wrk.coceso.service.hooks;

import at.wrk.coceso.entity.Incident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class IncidentSetEnded implements IncidentDoneHook {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentSetEnded.class);

    @Override
    public void call(final Incident incident) {
        LOG.debug("Incident state changed to 'done'. Ended-timestamp ist set.");
        incident.setEnded();
    }
}
