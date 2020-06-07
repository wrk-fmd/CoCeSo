package at.wrk.coceso.mapper;

import at.wrk.coceso.dto.message.ReceivedMessageDto;
import at.wrk.coceso.entity.ReceivedMessage;
import at.wrk.fmd.mls.message.dto.IncomingMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    ReceivedMessage dtoToMessage(IncomingMessageDto message);

    ReceivedMessageDto messageToDto(ReceivedMessage message);
}
