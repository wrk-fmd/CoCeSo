package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Concern;
import java.util.Collection;
import java.util.stream.Stream;

public interface IAutocomplete {

  public Collection<String> getAll(String filter, Integer max);

  public default Collection<String> getAll(String filter, Integer max, Concern concern) {
    return getAll(filter, max);
  }

  public Stream<String> getContaining(String filter, Integer max);

  public default Stream<String> getContaining(String filter, Integer max, Concern concern) {
    return getContaining(filter, max);
  }

}
