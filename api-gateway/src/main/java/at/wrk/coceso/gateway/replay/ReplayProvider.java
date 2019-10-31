package at.wrk.coceso.gateway.replay;

import java.util.List;

public interface ReplayProvider<T> {

    List<T> getMessages(String routingKey);

    String getName();
}
