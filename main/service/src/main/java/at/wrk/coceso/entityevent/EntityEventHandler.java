package at.wrk.coceso.entityevent;

public interface EntityEventHandler<T> {

  void entityChanged(T entity);

  void entityChanged(T entity, int concern);

  void entityDeleted(int id, int concern);

  EntityEventListener<T> addListener(EntityEventListener<T> listener);

  void removeListener(EntityEventListener<T> listener);

  int getHver();

  int getSeq(int concern);

  boolean matches(Class<?> type);

}
