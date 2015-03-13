package at.wrk.coceso.service;

import at.wrk.coceso.dao.SelcallDao;
import at.wrk.coceso.entity.Selcall;
import at.wrk.selcall.ReceivedMessageListener;
import at.wrk.selcall.TransceiverManager;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;

import at.wrk.selcall.TransceiverManagerImpl;
import at.wrk.selcall.TransceiverManagerMockup;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SelcallService implements ReceivedMessageListener {

  private static final Logger LOG = Logger.getLogger(SelcallService.class);

  TransceiverManager transceiverManager = TransceiverManagerMockup.getInstance();

  @Autowired
  private SelcallDao selcallDao;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @PostConstruct
  protected void init() {
    transceiverManager.addListener(this);
  }

  @Override
  public void handleMessage(String port, String message) {
    LOG.info(String.format("Call received from '%s'", message));

    Selcall incomingCall = new Selcall();
    incomingCall.setDirection(Selcall.Direction.RX);
    incomingCall.setTimestamp(Calendar.getInstance());
    incomingCall.setAni(message);
    incomingCall.setPort(port);

    messagingTemplate.convertAndSend("/topic/radio/incoming", incomingCall);
    selcallDao.save(incomingCall);
  }

  public List<Selcall> getLastMinutes(int minutes) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, -minutes);
    return selcallDao.findByTimestampGreaterThanAndDirection(cal, Selcall.Direction.RX);
  }

  public boolean sendSelcall(Selcall selcall) {
    if (selcall == null || selcall.getAni() == null) {
      LOG.info("Selcall or ANI is null");
      return false;
    }

    boolean success = true;
    try {
      if (selcall.getPort() == null) {
        LOG.debug(String.format("Trying to send Selcall to '%s' on all ports", selcall.getAni()));
        transceiverManager.send(selcall.getAni());
      } else {
        LOG.debug(String.format("Trying to send Selcall to '%s' on port '%s'", selcall.getAni(), selcall.getPort()));
        transceiverManager.send(selcall.getAni(), selcall.getPort());
      }
    } catch (IllegalArgumentException e) {
      success = false;
    }

    selcall.setTimestamp(Calendar.getInstance());
    selcall.setDirection(success ? Selcall.Direction.TX : Selcall.Direction.TX_FAILED);
    selcallDao.save(selcall);

    return success;
  }

  public Set<String> getPorts() {
    return transceiverManager.getPorts();
  }

}
