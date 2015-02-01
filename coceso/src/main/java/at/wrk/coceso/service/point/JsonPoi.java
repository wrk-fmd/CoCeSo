package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Point;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class JsonPoi extends PreloadedAutocomplete implements ILocate {

  private final Point[] points;

  public JsonPoi(Resource source) throws IOException {
    points = new ObjectMapper().readValue(source.getFile(), Point[].class);

    for (Point poi : points) {
      if (poi.getInfo() == null) {
        continue;
      }
      String key = poi.getInfo().toLowerCase().replaceAll("\n", ", ");
      autocomplete.put(key, poi.getInfo());
    }
  }

  @Override
  public boolean locate(Point p) {
    String info = p.getInfo().toLowerCase();
    Point match = null;
    for (Point poi : points) {
      if (poi.getLongitude() != 0 && poi.getLatitude() != 0 && info.startsWith(poi.getInfo().toLowerCase())) {
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
