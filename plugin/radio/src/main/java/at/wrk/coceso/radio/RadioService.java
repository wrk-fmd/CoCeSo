package at.wrk.coceso.radio;

import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import at.wrk.coceso.radio.Selcall.Direction;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

@Service
public class RadioService {

  private static final Logger LOG = LoggerFactory.getLogger(RadioService.class);

  @Autowired
  private SelcallRepository selcallRepository;

  private final Map<String, Port> ports;

  private final EntityEventListener<Selcall> webSocketWriter;

  private final EntityEventHandler<Selcall> entityEventHandler;

  private final String authToken;

  @Autowired
  public RadioService(Environment env, EntityEventFactory eef) {
    entityEventHandler = eef.getEntityEventHandler(Selcall.class);
    webSocketWriter = eef.getWebSocketWriter("/topic/radio/incoming", null, null);
    entityEventHandler.addListener(webSocketWriter);

    ports = new ConcurrentHashMap<>();
    try {
      PropertiesLoaderUtils.loadAllProperties("ports.properties")
          .forEach((path, name) -> ports.put((String) path, new Port((String) path, (String) name)));
    } catch (IOException e) {
      LOG.info("No port names found");
    }

    this.authToken = env.getProperty("radio.authenticationToken");
  }

  @PreDestroy
  private void destroy() {
    entityEventHandler.removeListener(webSocketWriter);
  }

  public boolean reloadPorts() {
    // No longer implemented
    return true;
  }

  public void receiveMessage(IncomingMessageDto message, String key) {
    if (authToken == null || !authToken.equals(key)) {
      LOG.debug("Received call with invalid authorization key");
      return;
    }

    LOG.debug("Call received from '{}'", message.getSender());

    // Add channel to port list, if it doesn't already exist
    ports.computeIfAbsent(message.getChannel(), k -> new Port(k, null));

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

  public List<Selcall> getLastMinutes(int minutes) {
    return selcallRepository.findByTimestampGreaterThanAndDirectionIn(
        OffsetDateTime.now().minusMinutes(minutes), EnumSet.of(Selcall.Direction.RX, Selcall.Direction.RX_EMG));
  }

  public boolean sendSelcall(Selcall selcall) {
    if (selcall == null || selcall.getAni() == null) {
      LOG.info("Selcall or ANI is null");
      return false;
    }

    boolean success = true;
    String port = selcall.getPort(), ani = selcall.getAni();
    try {
      if (port == null) {
        LOG.debug("Trying to send Selcall to '{}' on all ports", ani);
        // No longer implemented
      } else {
        LOG.debug("Trying to send Selcall to '{}' on port '{}'", ani, port);
        // No longer implemented
      }
    } catch (IllegalArgumentException e) {
      success = false;
    }

    selcall.setTimestamp(OffsetDateTime.now());
    selcall.setDirection(success ? Selcall.Direction.TX : Selcall.Direction.TX_FAILED);
    selcallRepository.save(selcall);

    return success;
  }

  public List<Port> getPorts() {
    return ports.values().stream()
        .sorted(Comparator.comparing(Port::getPath))
        .collect(Collectors.toList());
  }

}
