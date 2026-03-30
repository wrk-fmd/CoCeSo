package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.LogEntry;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogWebSocketPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(LogWebSocketPublisher.class);

    private final SimpMessagingTemplate messaging;

    @Autowired(required = false)
    public LogWebSocketPublisher(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    public void publish(final Concern concern, final LogEntry entry) {
        if (messaging == null) {
            LOG.debug("SimpMessagingTemplate not available; skipping websocket publish for log");
            return;
        }

        if (concern == null) {
            LOG.debug("No concern on LogEntry; skipping websocket publish");
            return;
        }

        try {
            if (messaging == null) {
                LOG.debug("SimpMessagingTemplate is not available; skipping publish");
                return;
            }
            String topic = String.format("/topic/log/%d", concern.getId());
            LOG.debug("Publishing LogEntry to topic {}: {}", topic, entry);
            messaging.convertAndSend(topic, entry);
        } catch (Exception e) {
            LOG.warn("Failed to publish LogEntry to websocket", e);
        }
    }
}
