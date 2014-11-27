package at.wrk.coceso.entity.helper;

import java.util.Map;

public class JsonContainer {

  private String type;
  private Map<String, ChangePair<Object>> data;

  public JsonContainer() {
    this(null, null);
  }

  public JsonContainer(String type, Map<String, ChangePair<Object>> data) {
    this.type = type;
    this.data = data;
  }

  public String getType() {
    return type;
  }

  public Map<String, ChangePair<Object>> getData() {
    return data;
  }

}
