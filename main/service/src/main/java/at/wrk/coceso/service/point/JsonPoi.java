package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Point;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class JsonPoi extends PreloadedAutocomplete implements ILocate {

  private final Collection<Point> points;

  public JsonPoi(ObjectMapper objectMapper, String path) throws IOException {
    this(objectMapper, new PathMatchingResourcePatternResolver().getResources(path));
  }

  public JsonPoi(ObjectMapper objectMapper, Resource... sources) throws IOException {
    points = new HashSet<>();

    for (Resource source : sources) {
      Point[] data = objectMapper.readValue(source.getInputStream(), Point[].class);

      for (Point poi : data) {
        if (poi.getInfo() == null) {
          continue;
        }
        points.add(poi);
        String key = poi.getInfo().toLowerCase().replaceAll("\n", ", ");
        autocomplete.put(key, poi.getInfo());
      }
    }
  }

  @Override
  public boolean locate(Point p) {
    String info = p.getInfo().toLowerCase();
    Point match = null;
    for (Point poi : points) {
      if (poi.getLongitude() != null && poi.getLatitude() != null && info.startsWith(poi.getInfo().toLowerCase())) {
        if (poi.getInfo().length() == info.length()) {
          // Exact match, use this point
          match = poi;
          break;
        }
        if (match == null || match.getInfo().length() < poi.getInfo().length()) {
          // First match or current poi is more accurate than previous match
          match = poi;
        }
      }
    }

    if (match != null) {
      p.setLongitude(match.getLongitude());
      p.setLatitude(match.getLatitude());
      return true;
    }
    return false;
  }

}
