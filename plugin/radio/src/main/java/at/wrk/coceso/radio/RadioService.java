package at.wrk.coceso.radio;

import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;

@Service
@PropertySource(value = "classpath:ports.properties", ignoreResourceNotFound = true)
public class RadioService implements SelcallListener {

  private static final Logger LOG = LoggerFactory.getLogger(RadioService.class);

  @Autowired
  private SelcallRepository selcallRepository;

  private final TransceiverManager transceivers;

  private final EntityEventListener<Selcall> webSocketWriter;

  private final EntityEventHandler<Selcall> entityEventHandler;

  @Autowired
  public RadioService(Environment env, EntityEventFactory eef) {
    entityEventHandler = eef.getEntityEventHandler(Selcall.class);
    webSocketWriter = eef.getWebSocketWriter("/topic/radio/incoming", null, null);
    entityEventHandler.addListener(webSocketWriter);
    transceivers = TransceiverManager.getInstance(env, entityEventHandler);
  }

  @PostConstruct
  private void init() {
    transceivers.addListener(this);
  }

  @PreDestroy
  private void destroy() {
    transceivers.removeListener(this);
    entityEventHandler.removeListener(webSocketWriter);
  }

  public boolean reloadPorts() {
    transceivers.reloadPorts();
    return true;
  }

  @Override
  public void saveCall(final Selcall call) {
    selcallRepository.save(call);
  }

  public List<Selcall> getLastMinutes(final int minutes) {
    return selcallRepository.findByTimestampGreaterThanAndDirectionIn(
        Instant.now().minus(Duration.ofMinutes(minutes)), EnumSet.of(Selcall.Direction.RX, Selcall.Direction.RX_EMG));
  }

  public boolean sendSelcall(final Selcall selcall) {
    if (selcall == null || selcall.getAni() == null) {
      LOG.info("Selcall or ANI is null");
      return false;
    }

    boolean success = true;
    String port = selcall.getPort(), ani = selcall.getAni();
    try {
      if (port == null) {
        LOG.debug("Trying to send Selcall to '{}' on all ports", ani);
        transceivers.send(ani);
      } else {
        LOG.debug("Trying to send Selcall to '{}' on port '{}'", ani, port);
        transceivers.send(port, ani);
      }
    } catch (IllegalArgumentException e) {
      success = false;
    }

    selcall.setTimestamp(Instant.now());
    selcall.setDirection(success ? Selcall.Direction.TX : Selcall.Direction.TX_FAILED);
    selcallRepository.save(selcall);

    return success;
  }

  public List<Port> getPorts() {
    return transceivers.getPorts();
  }

}
