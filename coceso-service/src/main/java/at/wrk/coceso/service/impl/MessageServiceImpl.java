package at.wrk.coceso.service.impl;

import at.wrk.coceso.config.MessageProperties;
import at.wrk.coceso.dto.incident.IncidentDto;
import at.wrk.coceso.dto.message.MessageChannelDto;
import at.wrk.coceso.dto.message.ReceivedMessageDto;
import at.wrk.coceso.dto.message.SendAlarmDto;
import at.wrk.coceso.dto.message.SendAlarmDto.AlarmRecipientsDto;
import at.wrk.coceso.dto.message.SendAlarmDto.AlarmTypeDto;
import at.wrk.coceso.dto.message.SendMessageDto;
import at.wrk.coceso.entity.Contact;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.ReceivedMessage;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.event.events.IncidentEvent;
import at.wrk.coceso.event.events.ReceivedMessageEvent;
import at.wrk.coceso.mapper.IncidentMapper;
import at.wrk.coceso.mapper.MessageMapper;
import at.wrk.coceso.repository.MessageRepository;
import at.wrk.coceso.service.MessageService;
import at.wrk.fmd.mls.event.EventBus;
import at.wrk.fmd.mls.message.dto.IncomingMessageDto;
import at.wrk.fmd.mls.message.dto.MessageExchangeNames;
import at.wrk.fmd.mls.message.dto.OutgoingMessageDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final MessageMapper messageMapper;
    private final IncidentMapper incidentMapper;
    private final MessageProperties properties;
    private final AmqpTemplate amqpTemplate;
    private final EventBus eventBus;

    @Autowired
    public MessageServiceImpl(final MessageRepository repository, final MessageMapper messageMapper, final IncidentMapper incidentMapper,
            final MessageProperties properties, final AmqpTemplate amqpTemplate, final EventBus eventBus) {
        this.repository = repository;
        this.messageMapper = messageMapper;
        this.incidentMapper = incidentMapper;
        this.properties = properties;
        this.amqpTemplate = amqpTemplate;
        this.eventBus = eventBus;
    }

    @Override
    public List<ReceivedMessageDto> getLast(TemporalAmount timespan) {
        return repository.findReceivedAfter(Instant.now().minus(timespan)).stream()
                .map(messageMapper::messageToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void receivedMessage(IncomingMessageDto data) {
        ReceivedMessage message = repository.save(messageMapper.dtoToMessage(data));
        eventBus.publish(new ReceivedMessageEvent(messageMapper.messageToDto(message)));
    }

    @Override
    public Collection<MessageChannelDto> getChannels() {
        return properties.getChannels();
    }

    @Override
    public Map<AlarmTypeDto, String> getAlarmTemplates() {
        return properties.getTemplates();
    }

    @Override
    public void sendAlarm(final Incident incident, final SendAlarmDto data) {
        Map<String, Set<String>> recipients = incident.getUnits().stream()
                .filter(t -> filterAlarmRecipients(t, data))
                .peek(t -> setAlarmTimestamp(t, data))
                .flatMap(t -> getContactsForUnit(t.getUnit()))
                .collect(groupRecipientsByType());
        sendMessage(recipients, data);

        // Send a notification for the updated incident
        IncidentDto incidentDto = incidentMapper.incidentToDto(incident);
        eventBus.publish(new IncidentEvent(incidentDto));
    }

    @Override
    public void sendMessage(Unit unit, SendMessageDto data) {
        Map<String, Set<String>> recipients = getContactsForUnit(unit).collect(groupRecipientsByType());
        sendMessage(recipients, data);
    }

    private void sendMessage(Map<String, Set<String>> recipientsByType, SendMessageDto data) {
        String prefix = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n";
        recipientsByType.forEach((type, recipients) -> sendMessage(type, recipients, prefix + data.getMessage()));
    }

    private void sendMessage(String type, Collection<String> recipients, String payload) {
        OutgoingMessageDto message = new OutgoingMessageDto(type, recipients, payload);
        amqpTemplate.convertAndSend(MessageExchangeNames.MESSAGE_OUTGOING, type, message);
        // TODO
//        eventBus.publish(new NotificationMessage(MessageExchangeNames.MESSAGE_OUTGOING, type, message));
    }

    private boolean filterAlarmRecipients(final Task task, final SendAlarmDto data) {
        if (data.getRecipients() == AlarmRecipientsDto.ALL) {
            return true;
        }

        if (data.getRecipients() == AlarmRecipientsDto.LIST) {
            if (data.getUnits() == null) {
                return false;
            }
            return data.getUnits().contains(task.getUnit().getId());
        }

        if (data.getRecipients() == AlarmRecipientsDto.UNSENT) {
            switch (data.getType()) {
                case ALARM:
                    return task.getAlarmSent() == null;
                case CASUS:
                    return task.getCasusSent() == null;
            }
            return false;
        }

        return false;
    }

    private void setAlarmTimestamp(Task task, SendAlarmDto data) {
        switch (data.getType()) {
            case ALARM:
                task.setAlarmSent(Instant.now());
                break;
            case CASUS:
                task.setCasusSent(Instant.now());
                break;
        }
    }

    private Stream<Contact> getContactsForUnit(final Unit unit) {
        Stream<Contact> unitContacts = unit.getContacts() != null
                ? unit.getContacts().stream()
                : Stream.empty();

        Stream<Contact> crewContacts = unit.getCrew() != null
                ? unit.getCrew().stream().flatMap(m -> m.getContacts() != null ? m.getContacts().stream() : Stream.empty())
                : Stream.empty();

        return Stream.concat(unitContacts, crewContacts);
    }

    private Collector<Contact, ?, Map<String, Set<String>>> groupRecipientsByType() {
        return Collectors.groupingBy(
                Contact::getType,
                Collectors.mapping(Contact::getData, Collectors.toSet())
        );
    }
}
