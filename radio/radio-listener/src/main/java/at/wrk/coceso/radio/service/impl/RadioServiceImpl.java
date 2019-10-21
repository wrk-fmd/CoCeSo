package at.wrk.coceso.radio.service.impl;

import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

import at.wrk.coceso.radio.SelcallListener;
import at.wrk.coceso.radio.entity.Port;
import at.wrk.coceso.radio.entity.RadioCall;
import at.wrk.coceso.radio.repository.RadioRepository;
import at.wrk.coceso.radio.service.RadioService;
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
class RadioServiceImpl implements RadioService, SelcallListener {

  private static final Logger LOG = LoggerFactory.getLogger(RadioServiceImpl.class);

  @Autowired
  private RadioRepository selcallRepository;

  private final TransceiverManager transceivers;

  private final EntityEventListener<RadioCall> webSocketWriter;

  private final EntityEventHandler<RadioCall> entityEventHandler;

  @Autowired
  public RadioServiceImpl(Environment env, EntityEventFactory eef) {
    entityEventHandler = eef.getEntityEventHandler(RadioCall.class);
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
  public void saveCall(RadioCall call) {
    selcallRepository.save(call);
  }

  @Override
  public List<RadioCall> getLastMinutes(int minutes) {
    return selcallRepository.findByTimestampGreaterThanAndDirectionIn(
        OffsetDateTime.now().minusMinutes(minutes), EnumSet.of(RadioCall.Direction.RX, RadioCall.Direction.RX_EMG));
  }

  @Override
  public boolean sendCall(RadioCall selcall) {
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
    selcall.setDirection(success ? RadioCall.Direction.TX : RadioCall.Direction.TX_FAILED);
    selcallRepository.save(selcall);

    return success;
  }

  public List<Port> getPorts() {
    return transceivers.getPorts();
  }

}
