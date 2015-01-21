package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Point;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JsonPoi implements IAutocomplete, ILocate {

  private final Point[] points;

  public JsonPoi(Resource source) throws IOException {
    points = new ObjectMapper().readValue(source.getFile(), Point[].class);
  }

  @Override
  public List<String> getAll(String filter, Integer max) {
    List<String> filtered = new LinkedList<>();
    for (Point poi : points) {
      if (poi.getInfo().toLowerCase().startsWith(filter)) {
        filtered.add(poi.getInfo());
      }
    }

    if (max == null || max > filtered.size()) {
      filtered.addAll(getContaining(filter, max == null ? null : max - filtered.size()));
    }
    return filtered;
  }

  @Override
  public List<String> getContaining(String filter, Integer max) {
    List<String> filtered = new LinkedList<>();
    for (Point poi : points) {
      if (max != null && filtered.size() >= max) {
        break;
      }
      if (poi.getInfo().toLowerCase().indexOf(filter) > 0) {
        filtered.add(poi.getInfo());
      }
    }
    return filtered;
  }

  @Override
  public boolean locate(Point p) {
    String info = p.getInfo().toLowerCase();
    for (Point poi : points) {
      if (poi.getLongitude() != 0 && poi.getLatitude() != 0 && poi.getInfo().toLowerCase().startsWith(info)) {
        p.setLongitude(poi.getLongitude());
        p.setLatitude(poi.getLatitude());
        return true;
      }
    }
    return false;
  }

}
