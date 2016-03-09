package at.wrk.coceso.entityevent;

public interface EntityEventListener<T> {

  public void entityChanged(T entity, int concern, int hver, int seq);

  public void entityDeleted(int id, int concern, int hver, int seq);

}
