package at.wrk.coceso.service.point;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MultipleAutocomplete implements IAutocomplete {

  private final List<IAutocomplete> autocomplete;

  public MultipleAutocomplete() {
    autocomplete = Collections.emptyList();
  }

  @Autowired(required = false)
  public MultipleAutocomplete(List<IAutocomplete> autocomplete) {
    this.autocomplete = autocomplete;
  }

  public MultipleAutocomplete(IAutocomplete... autocomplete) {
    this.autocomplete = Arrays.asList(autocomplete);
  }

  @Override
  public Collection<String> getAll(String filter, Integer max) {
    List<String> filtered = autocomplete.stream().flatMap(a -> a.getAll(filter, 0).stream()).collect(Collectors.toList());
    filtered.addAll(getContaining(filter, max == null ? null : max - filtered.size()).collect(Collectors.toList()));
    return filtered;
  }

  @Override
  public Stream<String> getContaining(String filter, Integer max) {
    if (max != null && max <= 0) {
      return Stream.empty();
    }

    Stream<String> filtered = autocomplete.stream().flatMap(a -> a.getContaining(filter, null));
    return max == null ? filtered : filtered.limit(max);
  }

}
