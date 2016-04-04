package at.wrk.coceso.entityevent;

import at.wrk.coceso.entity.ConcernBoundEntity;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityEventHandler<T> {

  private final static Set<EntityEventHandler<? extends Object>> instances = new HashSet<>();

  private final Class<T> type;
  private final Collection<EntityEventListener<T>> listeners;
  private final int hver;
  private final Map<Integer, Integer> seq;

  public static <T> EntityEventHandler<T> getInstance(Class<T> type) {
    for (EntityEventHandler<? extends Object> instance : instances) {
      if (instance.type.equals(type)) {
        return (EntityEventHandler<T>) instance;
      }
    }

    EntityEventHandler<T> instance = new EntityEventHandler<>(type);
    instances.add(instance);
    return instance;
  }

  private EntityEventHandler(Class<T> type) {
    this.type = type;
    this.listeners = new HashSet<>();

    // Use time since 2016-01-01 as handler version
    this.hver = (int) (System.currentTimeMillis() / 1000 - 1451602800);
    this.seq = new HashMap<>();
  }

  public synchronized void entityChanged(T entity) {
    Integer concern = null;
    if (entity instanceof ConcernBoundEntity) {
      concern = ((ConcernBoundEntity) entity).getConcern().getId();
    }
    entityChanged(entity, concern == null ? 0 : concern);
  }

  public synchronized void entityChanged(T entity, int concern) {
    int cseq = seq.getOrDefault(concern, 0) + 1;

    seq.put(concern, cseq);
    listeners.parallelStream().forEach(l -> l.entityChanged(entity, concern, hver, cseq));
  }

  public synchronized void entityDeleted(int id, int concern) {
    int cseq = seq.getOrDefault(concern, 0) + 1;
    seq.put(concern, cseq);
    listeners.parallelStream().forEach(l -> l.entityDeleted(id, concern, hver, cseq));
  }

  public EntityEventListener<T> addListener(EntityEventListener<T> listener) {
    listeners.add(listener);
    return listener;
  }

  public void removeListener(EntityEventListener<T> listener) {
    listeners.remove(listener);
  }

  public int getHver() {
    return hver;
  }

  public int getSeq(int concern) {
    return seq.getOrDefault(concern, 0);
  }

}
