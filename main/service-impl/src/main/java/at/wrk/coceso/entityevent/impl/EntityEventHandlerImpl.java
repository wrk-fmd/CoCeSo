package at.wrk.coceso.entityevent.impl;

import at.wrk.coceso.entity.ConcernBoundEntity;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.entityevent.EntityEventListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class EntityEventHandlerImpl<T> implements EntityEventHandler<T> {

  private final Class<T> type;
  private final Collection<EntityEventListener<T>> listeners;
  private final int hver;
  private final Map<Integer, Integer> seq;

  EntityEventHandlerImpl(Class<T> type, final List<EntityEventListener<T>> supportedServiceListeners) {
    this.type = type;
    this.listeners = new HashSet<>(supportedServiceListeners);

    // Use time since 2016-01-01 as handler version
    this.hver = (int) (System.currentTimeMillis() / 1000 - 1451602800);
    this.seq = new HashMap<>();
  }

  @Override
  public boolean matches(Class<?> type) {
    return this.type.equals(type);
  }

  @Override
  public synchronized void entityChanged(T entity) {
    Integer concern = null;
    if (entity instanceof ConcernBoundEntity) {
      concern = ((ConcernBoundEntity) entity).getConcern().getId();
    }
    entityChanged(entity, concern == null ? 0 : concern);
  }

  @Override
  public synchronized void entityChanged(T entity, int concern) {
    int cseq = seq.getOrDefault(concern, 0) + 1;

    seq.put(concern, cseq);
    listeners.parallelStream().forEach(l -> l.entityChanged(entity, concern, hver, cseq));
  }

  @Override
  public synchronized void entityDeleted(int id, int concern) {
    int cseq = seq.getOrDefault(concern, 0) + 1;
    seq.put(concern, cseq);
    listeners.parallelStream().forEach(l -> l.entityDeleted(id, concern, hver, cseq));
  }

  @Override
  public EntityEventListener<T> addListener(EntityEventListener<T> listener) {
    listeners.add(listener);
    return listener;
  }

  @Override
  public void removeListener(EntityEventListener<T> listener) {
    listeners.remove(listener);
  }

  @Override
  public int getHver() {
    return hver;
  }

  @Override
  public int getSeq(int concern) {
    return seq.getOrDefault(concern, 0);
  }

}
