package at.wrk.selcall;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractTransceiverManager implements TransceiverManager, ReceivedMessageListener {

  private final Set<ReceivedMessageListener> listeners = new HashSet<>();

  @Override
  public void addListener(ReceivedMessageListener listener) {
    listeners.add(listener);
  }

  @Override
  public final void handleMessage(String port, String message) {
    for (ReceivedMessageListener listener : listeners) {
      listener.handleMessage(port, message);
    }
  }

  @Override
  public final void send(String message) {
    for (String port : getPorts()) {
      send(port, message);
    }
  }

}
