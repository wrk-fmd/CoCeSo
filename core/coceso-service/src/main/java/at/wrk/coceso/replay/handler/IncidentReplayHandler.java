package at.wrk.coceso.replay.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.IncidentService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class IncidentReplayHandler extends ConcernBoundReplayHandler<IncidentDto> {

    private final IncidentService incidentService;

    @Autowired
    public IncidentReplayHandler(final IncidentService incidentService, final ConcernService concernService,
            final AmqpTemplate amqpTemplate) {
        super(concernService, amqpTemplate, CocesoExchangeNames.STOMP_INCIDENTS);
        this.incidentService = incidentService;
    }

    @Override
    protected Collection<IncidentDto> getData(Concern concern) {
        return incidentService.getAllRelevant(concern);
    }
}
