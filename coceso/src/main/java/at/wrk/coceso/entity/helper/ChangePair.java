package at.wrk.coceso.entity.helper;

public class ChangePair<T> {

  private final T oldValue, newValue;

  public ChangePair() {
    oldValue = null;
    newValue = null;
  }

  public ChangePair(T oldValue, T newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public T getOldValue() {
    return oldValue;
  }

  public T getNewValue() {
    return newValue;
  }

}
