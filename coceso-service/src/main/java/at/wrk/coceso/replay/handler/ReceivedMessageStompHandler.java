package at.wrk.coceso.replay.handler;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.coceso.dto.message.ReceivedMessageDto;
import at.wrk.coceso.service.MessageService;
import at.wrk.fmd.mls.amqp.handler.AbstractAmqpHandler;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;

@Component
class ReceivedMessageStompHandler extends AbstractAmqpHandler<ReceivedMessageDto> {

    private static final Duration duration = Duration.ofMinutes(5);
    private final MessageService messageService;

    @Autowired
    public ReceivedMessageStompHandler(final MessageService messageService, final AmqpTemplate amqpTemplate) {
        super(amqpTemplate, CocesoExchangeNames.STOMP_MESSAGES);
        this.messageService = messageService;
    }

    @Override
    protected Collection<ReceivedMessageDto> getData(final String routingKey) {
        return messageService.getLast(duration);
    }
}
