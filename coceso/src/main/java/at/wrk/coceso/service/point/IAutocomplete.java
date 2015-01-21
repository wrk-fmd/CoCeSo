package at.wrk.coceso.service.point;

import java.util.List;

public interface IAutocomplete {

  public List<String> getAll(String filter, Integer max);
  public List<String> getContaining(String filter, Integer max);
}
