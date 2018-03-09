package at.wrk.coceso.entityevent;

import java.util.function.Function;

public interface EntityEventFactory {

  <T> EntityEventHandler<T> getEntityEventHandler(Class<T> type);

  <T> EntityEventListener<T> getWebSocketWriter(String url, Class<?> jsonView, Function<T, Integer> delete);

}
