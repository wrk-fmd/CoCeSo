package at.wrk.coceso.entity.types;

import java.util.Map;

public class MapUserType extends JsonUserType<Map> {

  @Override
  public Class<Map> returnedClass() {
    return Map.class;
  }

}
