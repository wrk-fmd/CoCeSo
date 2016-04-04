package at.wrk.coceso.radio;

import at.wrk.coceso.entityevent.EntityEventHandler;
import gnu.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * RXTX from http://create-lab-commons.googlecode.com/svn/trunk/java/lib/rxtx/
 */
public class TransceiverManager {

  private final static Logger LOG = LoggerFactory.getLogger(TransceiverManager.class);

  private static TransceiverManager instance;

  private final HashMap<String, Transceiver> transceivers;
  private List<Port> ports;
  private final Set<SelcallListener> listeners;
  private final Environment properties;

  private final EntityEventHandler<Selcall> entityEventHandler = EntityEventHandler.getInstance(Selcall.class);

  private TransceiverManager(Environment properties) {
    this.properties = properties;
    transceivers = new HashMap<>();
    listeners = new HashSet<>();
    reloadPorts();
  }

  public static synchronized TransceiverManager getInstance(Environment properties) {
    if (instance == null) {
      instance = new TransceiverManager(properties);
    }
    return instance;
  }

  public void addListener(SelcallListener listener) {
    listeners.add(listener);
  }

  public void removeListener(SelcallListener listener) {
    listeners.remove(listener);
    if (listeners.isEmpty()) {
      shutdown();
    }
  }

  public void handleCall(Selcall call) {
    LOG.info("Call received from '{}'", call.getAni());
    entityEventHandler.entityChanged(call, 0);
    listeners.stream().findAny().ifPresent(l -> l.saveCall(call));
  }

  public void send(String port, String message) {
    if (!transceivers.containsKey(port)) {
      LOG.warn("Tried to send message '{}' to non-existing port '{}'", message, port);
      throw new IllegalArgumentException("Port does not exist!");
    }
    transceivers.get(port).sendMessage(message);
  }

  public void send(String message) {
    transceivers.values().forEach(transceiver -> {
      transceiver.sendMessage(message);
    });
  }

  public List<Port> getPorts() {
    if (ports == null) {
      ports = new LinkedList<>();
      transceivers.values().forEach(transceiver -> {
        ports.add(transceiver.getPort());
      });
    }
    return ports;
  }

  public final synchronized void reloadPorts() {
    shutdown();

    LOG.info("(Re)detecting ports...");
    Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
    while (portEnum.hasMoreElements()) {
      CommPortIdentifier portIdentifier = portEnum.nextElement();
      if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        try {
          String name = portIdentifier.getName();
          Transceiver t = new Transceiver(portIdentifier, this, properties.getProperty(name));
          transceivers.put(name, t);
        } catch (IllegalArgumentException e) {
          // Port in use
          // intended NOP
        }
      }
    }
  }

  private synchronized void shutdown() {
    LOG.info("Closing all ports...");
    transceivers.values().forEach((t) -> {
      t.closeSerialPort();
    });
    transceivers.clear();
    ports = null;
  }

}
