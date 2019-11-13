package at.wrk.coceso.stomp.replay.impl;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.GET;

import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.queues.RadioQueueNames;
import at.wrk.coceso.stomp.replay.ReplayProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class RadioReplayProvider implements ReplayProvider<ReceivedCallDto> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int MINUTES = 5;

    private final RestTemplate radioTemplate;

    public RadioReplayProvider(RestTemplate radioTemplate) {
        this.radioTemplate = requireNonNull(radioTemplate, "Radio RestTemplate must not be null");
    }

    @Override
    public List<ReceivedCallDto> getMessages(String routingKey) {
        LOG.debug("Loading received radio calls for last {} minutes", MINUTES);
        return radioTemplate.exchange("/calls/last/{minutes}", GET, null, receivedListType(), MINUTES).getBody();
    }

    private ParameterizedTypeReference<List<ReceivedCallDto>> receivedListType() {
        return new ParameterizedTypeReference<>() {
        };
    }

    @Override
    public String getName() {
        return RadioQueueNames.CALLS_RECEIVED;
    }
}
