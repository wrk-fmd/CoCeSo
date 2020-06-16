package at.wrk.coceso.replay.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.container.ContainerDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.ContainerService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class ContainerStompHandler extends ConcernBoundAmqpHandler<ContainerDto> {

    private final ContainerService containerService;

    @Autowired
    public ContainerStompHandler(final ContainerService containerService, final ConcernService concernService,
            final AmqpTemplate amqpTemplate) {
        super(concernService, amqpTemplate, CocesoExchangeNames.STOMP_CONTAINERS);
        this.containerService = containerService;
    }

    @Override
    protected Collection<ContainerDto> getData(Concern concern) {
        return containerService.getAll(concern);
    }
}
