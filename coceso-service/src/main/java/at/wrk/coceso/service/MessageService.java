package at.wrk.coceso.service;

import at.wrk.coceso.dto.message.MessageChannelDto;
import at.wrk.coceso.dto.message.ReceivedMessageDto;
import at.wrk.coceso.dto.message.SendAlarmDto;
import at.wrk.coceso.dto.message.SendAlarmDto.AlarmTypeDto;
import at.wrk.coceso.dto.message.SendMessageDto;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.fmd.mls.message.dto.IncomingMessageDto;

import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MessageService {

    List<ReceivedMessageDto> getLast(TemporalAmount timespan);

    void receivedMessage(IncomingMessageDto data);

    Collection<MessageChannelDto> getChannels();

    Map<AlarmTypeDto, String> getAlarmTemplates();

    void sendAlarm(Incident incident, SendAlarmDto data);

    void sendMessage(Unit unit, SendMessageDto data);
}
