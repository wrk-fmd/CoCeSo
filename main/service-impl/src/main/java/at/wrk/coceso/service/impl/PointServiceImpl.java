package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.geocode.autocomplete.AutocompleteSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
class PointServiceImpl implements PointService {

  private static final int AUTOC_LEN = 20;

  @Autowired
  private PatadminService patadminService;

  @Autowired
  @Qualifier("ChainedAutocomplete")
  private AutocompleteSupplier<?> autocomplete;

  @Override
  public Collection<String> autocomplete(String filter) {
    return autocomplete(filter, null);
  }

  @Override
  public Collection<String> autocomplete(String filter, Concern concern) {
    String normalizedLowercaseFilter = AutocompleteSupplier.getKey(filter);
    if (normalizedLowercaseFilter == null || normalizedLowercaseFilter.length() <= 1) {
      return null;
    }

    if (Concern.isClosed(concern)) {
      Collection<String> filtered = autocomplete.getStartStringCollection(normalizedLowercaseFilter);
      filtered.addAll(autocomplete.getContainingStringCollection(normalizedLowercaseFilter, AUTOC_LEN - filtered.size()));
      return filtered;
    }

    List<Unit> treatmentGroups = patadminService.getGroups(concern);
    Collection<String> filtered = Stream.concat(
        treatmentGroups.stream()
                .map(Unit::getCall)
                .filter(u -> u.toLowerCase().startsWith(normalizedLowercaseFilter)),
        autocomplete.getStartString(normalizedLowercaseFilter)
    ).collect(Collectors.toList());

    if (filtered.size() < AUTOC_LEN) {
      filtered.addAll(Stream.concat(
          treatmentGroups.stream()
                  .map(Unit::getCall)
                  .filter(u -> containsButDoesNotStartWith(u, normalizedLowercaseFilter)),
          autocomplete.getContainingString(normalizedLowercaseFilter, null)
      ).limit(AUTOC_LEN - filtered.size()).collect(Collectors.toList()));
    }

    return filtered;
  }

  private boolean containsButDoesNotStartWith(final String inputToCheck, final String searchString) {
    return inputToCheck.toLowerCase().indexOf(searchString) > 0;
  }
}
