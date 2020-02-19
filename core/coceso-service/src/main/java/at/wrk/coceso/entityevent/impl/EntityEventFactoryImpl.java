package at.wrk.coceso.entityevent.impl;

import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class EntityEventFactoryImpl implements EntityEventFactory {

    private final static Set<EntityEventHandler<?>> instances = new HashSet<>();

    private final SocketMessagingTemplate template;
    private final Collection<EntityEventListener<?>> serviceEntityEventListeners;

    @Autowired
    public EntityEventFactoryImpl(
            final SocketMessagingTemplate template) {
        this.template = template;
        this.serviceEntityEventListeners = new ArrayList<>();
    }

    @Autowired(required = false)
    public void setServiceEntityEventListeners(final Collection<EntityEventListener<?>> listeners) {
        if (listeners != null) {
            this.serviceEntityEventListeners.addAll(listeners);
        }
    }

    @Override
    public <T> EntityEventHandler<T> getEntityEventHandler(Class<T> type) {
        EntityEventHandler<T> instance = null;
        for (EntityEventHandler<?> existingInstance : instances) {
            if (existingInstance.matches(type)) {
                instance = (EntityEventHandler<T>) existingInstance;
            }
        }

        if (instance == null) {
            List<EntityEventListener<T>> supportedServiceListeners = fillterSupportedListeners(type);
            instance = new EntityEventHandlerImpl<>(type, supportedServiceListeners);
            instances.add(instance);
        }

        return instance;
    }

    private <T> List<EntityEventListener<T>> fillterSupportedListeners(final Class<T> type) {
        return serviceEntityEventListeners
                .stream()
                .filter(entityEventListener -> entityEventListener.isSupported(type))
                .map(listener -> (EntityEventListener<T>) listener)
                .collect(Collectors.toList());
    }

    @Override
    public <T> WebSocketWriter<T> getWebSocketWriter(final String url, final Class<?> jsonView, final Function<T, Integer> delete) {
        return new WebSocketWriter<>(template, url, jsonView, delete);
    }
}
