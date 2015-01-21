package at.wrk.coceso.service.point;

import java.util.LinkedList;
import java.util.List;

public class MultipleAutocomplete implements IAutocomplete {

  private final IAutocomplete[] autocomplete;

  public MultipleAutocomplete(IAutocomplete... autocomplete) {
    this.autocomplete = autocomplete;
  }

  @Override
  public List<String> getAll(String filter, Integer max) {
    List<String> filtered = new LinkedList<>();
    for (IAutocomplete a : autocomplete) {
      filtered.addAll(a.getAll(filter, 0));
    }
    if (max == null || max > filtered.size()) {
      filtered.addAll(getContaining(filter, max - filtered.size()));
    }
    return filtered;
  }

  @Override
  public List<String> getContaining(String filter, Integer max) {
    List<String> filtered = new LinkedList<>();

    for (IAutocomplete a : autocomplete) {
      if (max != null && filtered.size() >= max) {
        break;
      }
      filtered.addAll(a.getContaining(filter, max == null ? null : max - filtered.size()));
    }
    return filtered;
  }

}
