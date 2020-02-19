package at.wrk.coceso.entity.helper;

public class RestProperty {

  private final String key;
  private final Object value;

  public RestProperty(String key, Object value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

}
