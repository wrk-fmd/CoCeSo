package at.wrk.coceso.replay.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.concern.ConcernDto;
import at.wrk.coceso.service.ConcernService;
import at.wrk.fmd.mls.amqp.handler.AbstractAmqpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
class ConcernStompHandler extends AbstractAmqpHandler<ConcernDto> {

    private final ConcernService concernService;

    @Autowired
    public ConcernStompHandler(final ConcernService concernService, final AmqpTemplate amqpTemplate) {
        super(amqpTemplate, CocesoExchangeNames.STOMP_CONCERNS);
        this.concernService = concernService;
    }

    @Override
    protected Collection<ConcernDto> getData(String routingKey) {
        return concernService.getAll();
    }
}
