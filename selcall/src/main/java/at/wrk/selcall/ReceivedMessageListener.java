package at.wrk.selcall;

/**
 * Created by Robert on 12.06.2014.
 */
public interface ReceivedMessageListener {
    public void handleMessage(String port, String message);
}
