package at.wrk.coceso.service.point;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class PreloadedAutocomplete implements IAutocomplete {

  protected final TreeMap<String, String> autocomplete;

  public PreloadedAutocomplete() {
    autocomplete = new TreeMap<>();
  }

  @Override
  public List<String> getAll(String filter, Integer max) {
    String to = filter.substring(0, filter.length() - 1) + (char) (filter.charAt(filter.length() - 1) + 1);
    List<String> filtered = new LinkedList<>(autocomplete.subMap(filter, to).values());

    if (max == null || max > filtered.size()) {
      filtered.addAll(getContaining(filter, max == null ? null : max - filtered.size()));
    }
    return filtered;
  }

  @Override
  public List<String> getContaining(String filter, Integer max) {
    List<String> filtered = new LinkedList<>();
    for (Map.Entry<String, String> entry : autocomplete.entrySet()) {
      if (max != null && filtered.size() >= max) {
        break;
      }
      if (entry.getKey().indexOf(filter) > 0) {
        filtered.add(entry.getValue());
      }
    }
    return filtered;
  }

}
