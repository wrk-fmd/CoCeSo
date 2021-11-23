package at.wrk.coceso.replay.handler;

import at.wrk.coceso.endpoint.ParamValidator;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.exceptions.ConcernClosedException;
import at.wrk.coceso.exceptions.NotFoundException;
import at.wrk.coceso.service.ConcernService;
import at.wrk.fmd.mls.amqp.event.AmqpEvent;
import at.wrk.fmd.mls.amqp.handler.AbstractAmqpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;

@Slf4j
abstract class ConcernBoundAmqpHandler<T> extends AbstractAmqpHandler<T> {

    private final ConcernService concernService;

    public ConcernBoundAmqpHandler(final ConcernService concernService, final AmqpTemplate amqpTemplate, final String target) {
        super(amqpTemplate, target);
        this.concernService = concernService;
    }

    @Override
    @Transactional
    public void handle(AmqpEvent event) {
        super.handle(event);
    }

    @Override
    protected Collection<T> getData(String routingKey) {
        Long id = parseIdFromKey(routingKey, 0);
        if (id == null) {
            return Collections.emptyList();
        }

        try {
            Concern concern = concernService.getConcern(id).orElse(null);
            ParamValidator.open(concern);
            return getData(concern);
        } catch (NotFoundException e) {
            log.info("Tried to replay data for non-existent concern {}", id, e);
            return Collections.emptyList();
        } catch (ConcernClosedException e) {
            log.info("Tried to replay data for closed concern {}", id, e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error loading replay data for concern {}", id, e);
            return Collections.emptyList();
        }
    }

    protected abstract Collection<T> getData(Concern concern);
}
