package at.wrk.coceso.service.point;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PreloadedAutocomplete implements IAutocomplete {

  protected final TreeMap<String, String> autocomplete;

  public PreloadedAutocomplete(TreeMap autocomplete) {
    this.autocomplete = autocomplete;
  }

  public PreloadedAutocomplete() {
    autocomplete = new TreeMap<>();
  }

  @Override
  public Collection<String> getAll(String filter, Integer max) {
    String to = filter.substring(0, filter.length() - 1) + (char) (filter.charAt(filter.length() - 1) + 1);
    Collection<String> filtered = autocomplete.subMap(filter, to).values();
    filtered.addAll(getContaining(filter, max == null ? null : max - filtered.size()).collect(Collectors.toList()));
    return filtered;
  }

  @Override
  public Stream<String> getContaining(String filter, Integer max) {
    if (max != null && max <= 0) {
      return Stream.empty();
    }

    Stream<String> filtered = autocomplete.entrySet().stream()
        .filter(e -> e.getKey().indexOf(filter) > 0)
        .map(Map.Entry::getValue);
    return max == null ? filtered : filtered.limit(max);
  }

}
