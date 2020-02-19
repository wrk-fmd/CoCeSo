package at.wrk.coceso.entityevent;

public interface EntityEventListener<T> {

    void entityChanged(T entity, int concern, int hver, int seq);

    void entityDeleted(int id, int concern, int hver, int seq);

    boolean isSupported(Class<?> supportedClass);
}
