package at.wrk.coceso.event.listener;

import at.wrk.coceso.service.MessageService;
import at.wrk.fmd.mls.message.dto.IncomingMessageDto;
import at.wrk.fmd.mls.message.dto.MessageExchangeNames;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReceivedMessageListener {

    private final MessageService messageService;

    public ReceivedMessageListener(MessageService messageService) {
        this.messageService = messageService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = MessageExchangeNames.MESSAGE_INCOMING, type = "fanout")
    ))
    public void listen(IncomingMessageDto message) {
        messageService.receivedMessage(message);
    }
}
