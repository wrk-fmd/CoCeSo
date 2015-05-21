package at.wrk.selcall;

import java.util.Set;

public interface TransceiverManager {

  public Set<String> getPorts();

  public void addListener(ReceivedMessageListener listener);

  public void send(String message);

  public void send(String port, String message);

  public void shutdown();
}
