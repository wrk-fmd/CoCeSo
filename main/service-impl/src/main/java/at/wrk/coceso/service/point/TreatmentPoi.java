package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.patadmin.PatadminService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
class TreatmentPoi implements IAutocomplete, ILocate {

  @Autowired
  private PatadminService patadminService;

  @Override
  public Collection<String> getAll(String filter, Integer max) {
    return Collections.emptySet();
  }

  @Override
  public Collection<String> getAll(String filter, Integer max, Concern concern) {
    if (Concern.isClosed(concern)) {
      return Collections.emptySet();
    }

    List<Unit> groups = patadminService.getGroups(concern);
    Collection<String> filtered = groups.stream()
        .map(Unit::getCall)
        .filter(u -> u.toLowerCase().startsWith(filter))
        .collect(Collectors.toList());
    filtered.addAll(getContaining(filter, max == null ? null : max - filtered.size(), groups).collect(Collectors.toList()));
    return filtered;
  }

  @Override
  public Stream<String> getContaining(String filter, Integer max) {
    return Stream.empty();
  }

  @Override
  public Stream<String> getContaining(String filter, Integer max, Concern concern) {
    return Concern.isClosed(concern)
        ? Stream.empty()
        : getContaining(filter, max, patadminService.getGroups(concern));
  }

  private Stream<String> getContaining(String filter, Integer max, List<Unit> groups) {
    if (max != null && max <= 0) {
      return Stream.empty();
    }

    Stream<String> filtered = groups.stream()
        .filter(u -> u.getCall().indexOf(filter) > 0)
        .map(Unit::getCall);
    return max == null ? filtered : filtered.limit(max);
  }

  @Override
  public boolean locate(Point p) {
    return false;
  }

  @Override
  public boolean locate(Point p, Concern concern) {
    if (Concern.isClosed(concern)) {
      return false;
    }

    List<Unit> groups = patadminService.getGroups(concern);
    String info = p.getInfo().toLowerCase();

    Optional<Point> location = groups.stream()
        .filter(u -> info.startsWith(u.getCall().toLowerCase()))
        .map(Unit::getHome)
        .filter(loc -> loc != null && loc.getLatitude() != null && loc.getLongitude() != null)
        .findFirst();
    location.ifPresent(loc -> p.setLatLong(loc));
    return location.isPresent();
  }

}
