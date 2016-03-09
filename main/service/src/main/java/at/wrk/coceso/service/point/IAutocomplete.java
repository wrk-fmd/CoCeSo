package at.wrk.coceso.service.point;

import java.util.Collection;
import java.util.stream.Stream;

public interface IAutocomplete {

  public Collection<String> getAll(String filter, Integer max);

  public Stream<String> getContaining(String filter, Integer max);

}
