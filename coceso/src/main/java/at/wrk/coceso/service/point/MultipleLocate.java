package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Point;

public class MultipleLocate implements ILocate {

  private final ILocate[] locate;

  public MultipleLocate(ILocate... locate) {
    this.locate = locate;
  }

  @Override
  public boolean locate(Point p) {
    for (ILocate l : locate) {
      if (l.locate(p)) {
        return true;
      }
    }

    return false;
  }

}
