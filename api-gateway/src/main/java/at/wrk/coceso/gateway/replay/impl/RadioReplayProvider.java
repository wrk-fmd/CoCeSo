package at.wrk.coceso.gateway.replay.impl;

import at.wrk.coceso.gateway.replay.ReplayProvider;
import at.wrk.coceso.gateway.restclient.RadioClient;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.queues.RadioQueueNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

@Component
public class RadioReplayProvider implements ReplayProvider<ReceivedCallDto> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int MINUTES = 5;

    private final RadioClient client;

    public RadioReplayProvider(RadioClient client) {
        this.client = Objects.requireNonNull(client, "RadioClient must not be null");
    }

    @Override
    public List<ReceivedCallDto> getMessages(String routingKey) {
        LOG.debug("Loading received radio calls for last {} minutes", MINUTES);
        return client.getLast(MINUTES);
    }

    @Override
    public String getName() {
        return RadioQueueNames.CALLS_RECEIVED;
    }
}
