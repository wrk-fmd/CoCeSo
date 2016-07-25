package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MultipleLocate implements ILocate {

  private final List<ILocate> locate;

  public MultipleLocate() {
    locate = Collections.emptyList();
  }

  @Autowired(required = false)
  public MultipleLocate(List<ILocate> locate) {
    this.locate = locate;
  }

  public MultipleLocate(ILocate... locate) {
    this.locate = Arrays.asList(locate);
  }

  @Override
  public boolean locate(Point p) {
    return locate(p, null);
  }

  @Override
  public boolean locate(Point p, Concern concern) {
    return locate.stream().anyMatch((l) -> (l.locate(p, concern)));
  }

}
