package at.wrk.coceso.radio;

import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.SocketMessagingTemplate;
import at.wrk.coceso.entityevent.WebSocketWriter;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:ports.properties", ignoreResourceNotFound = true)
public class RadioService implements SelcallListener {

  private static final Logger LOG = LoggerFactory.getLogger(RadioService.class);

  @Autowired
  private SelcallRepository selcallRepository;

  private final TransceiverManager transceivers;

  private final WebSocketWriter<Selcall> webSocketWriter;

  @Autowired
  public RadioService(Environment env, SocketMessagingTemplate template) {
    transceivers = TransceiverManager.getInstance(env);

    webSocketWriter = new WebSocketWriter<>(template, "/topic/radio/incoming", null, null);
    EntityEventHandler.getInstance(Selcall.class).addListener(webSocketWriter);
  }

  @PostConstruct
  private void init() {
    transceivers.addListener(this);
  }

  @PreDestroy
  private void destroy() {
    transceivers.removeListener(this);
    EntityEventHandler.getInstance(Selcall.class).removeListener(webSocketWriter);
  }

  public boolean reloadPorts() {
    transceivers.reloadPorts();
    return true;
  }

  @Override
  public void saveCall(Selcall call) {
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
        transceivers.send(ani);
      } else {
        LOG.debug("Trying to send Selcall to '{}' on port '%s'", ani, port);
        transceivers.send(port, ani);
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
    return transceivers.getPorts();
  }

}
