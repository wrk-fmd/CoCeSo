package at.wrk.coceso.entityevent.impl;

import at.wrk.coceso.entityevent.EntityEventHandler;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;
import at.wrk.coceso.entityevent.EntityEventFactory;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;

@Component
class EntityEventFactoryImpl implements EntityEventFactory {

  private final static Set<EntityEventHandler<? extends Object>> instances = new HashSet<>();

  @Autowired
  private SocketMessagingTemplate template;

  @Override
  public <T> EntityEventHandler<T> getEntityEventHandler(Class<T> type) {
    for (EntityEventHandler<? extends Object> instance : instances) {
      if (instance.matches(type)) {
        return (EntityEventHandler<T>) instance;
      }
    }

    EntityEventHandler<T> instance = new EntityEventHandlerImpl<>(type);
    instances.add(instance);
    return instance;
  }

  @Override
  public <T> WebSocketWriter<T> getWebSocketWriter(String url, Class<?> jsonView, Function<T, Integer> delete) {
    return new WebSocketWriter<>(template, url, jsonView, delete);
  }

}
