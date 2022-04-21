package at.wrk.coceso.radio;

import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.radio.Selcall.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RadioService {
    private static final Logger LOG = LoggerFactory.getLogger(RadioService.class);

    private final SelcallRepository selcallRepository;
    private final Map<String, Port> ports;
    private final EntityEventListener<Selcall> webSocketWriter;
    private final EntityEventHandler<Selcall> entityEventHandler;


    @Autowired
    public RadioService(final EntityEventFactory entityEventFactory, final SelcallRepository selcallRepository) {
        this.selcallRepository = selcallRepository;
        entityEventHandler = entityEventFactory.getEntityEventHandler(Selcall.class);
        webSocketWriter = entityEventFactory.getWebSocketWriter("/topic/radio/incoming", null, null);
        entityEventHandler.addListener(webSocketWriter);

        ports = new ConcurrentHashMap<>();
        try {
            PropertiesLoaderUtils.loadAllProperties("ports.properties")
                    .forEach((path, name) -> ports.put((String) path, new Port((String) path, (String) name)));
        } catch (IOException e) {
            LOG.info("Failed to load radio port names: {}", e.getMessage());
        }
    }

    @PreDestroy
    private void destroy() {
        entityEventHandler.removeListener(webSocketWriter);
    }

    public void receiveMessage(final IncomingMessageDto message) {
        LOG.debug("Received new message from radio: {}", message);

        // Add channel to port list, if it doesn't already exist
        ports.computeIfAbsent(message.getChannel(), path -> new Port(path, null));

        // Transform to internal entity
        Selcall call = new Selcall(
                message.getChannel(),
                message.getSender(),
                message.isEmergency() ? Direction.RX_EMG : Direction.RX,
                message.getTimestamp().atOffset(ZoneOffset.UTC)
        );
        entityEventHandler.entityChanged(call, 0);
        selcallRepository.save(call);
    }

    public List<Selcall> getLastMinutes(final int minutes) {
        return selcallRepository.findByTimestampGreaterThanAndDirectionIn(
                OffsetDateTime.now().minusMinutes(minutes),
                EnumSet.of(Selcall.Direction.RX, Selcall.Direction.RX_EMG));
    }

    public List<Port> getPorts() {
        return ports.values()
                .stream()
                .sorted(Comparator.comparing(Port::getPath))
                .collect(Collectors.toList());
    }
}
