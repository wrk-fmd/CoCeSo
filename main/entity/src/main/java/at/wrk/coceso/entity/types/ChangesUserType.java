package at.wrk.coceso.entity.types;

import at.wrk.coceso.entity.helper.Changes;

public class ChangesUserType extends JsonUserType<Changes> {

  @Override
  public Class<Changes> returnedClass() {
    return Changes.class;
  }

}
