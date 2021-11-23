package at.wrk.coceso.replay.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.UnitService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class UnitStompHandler extends ConcernBoundAmqpHandler<UnitDto> {

    private final UnitService unitService;

    @Autowired
    public UnitStompHandler(final UnitService unitService, final ConcernService concernService, final AmqpTemplate amqpTemplate) {
        super(concernService, amqpTemplate, CocesoExchangeNames.STOMP_UNITS);
        this.unitService = unitService;
    }

    @Override
    protected Collection<UnitDto> getData(Concern concern) {
        return unitService.getAll(concern);
    }
}
