package at.wrk.coceso.config;

import at.wrk.coceso.dto.message.MessageChannelDto;
import at.wrk.coceso.dto.message.SendAlarmDto.AlarmTypeDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties("application.messages")
public class MessageProperties {

    private Map<AlarmTypeDto, String> templates;
    private List<MessageChannelDto> channels;
}
