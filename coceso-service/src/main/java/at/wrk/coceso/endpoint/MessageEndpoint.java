package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.message.MessageChannelDto;
import at.wrk.coceso.dto.message.ReceivedMessageDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Collection;

@RestController
@RequestMapping("/concerns/{concern}/messages")
public class MessageEndpoint {

    private final MessageService messageService;

    @Autowired
    public MessageEndpoint(final MessageService messageService) {
        this.messageService = messageService;
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).MESSAGE_READ)")
    @GetMapping("/{minutes}")
    public Collection<ReceivedMessageDto> getLastMessage(@PathVariable final Concern concern, @PathVariable long minutes) {
        ParamValidator.open(concern);
        return messageService.getLast(Duration.ofMinutes(minutes));
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).MESSAGE_READ)")
    @GetMapping("/channels")
    public Collection<MessageChannelDto> getChannels(@PathVariable final Concern concern) {
        ParamValidator.open(concern);
        return messageService.getChannels();
    }
}
