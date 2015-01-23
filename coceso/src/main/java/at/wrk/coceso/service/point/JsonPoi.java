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
    String info = p.getInfo().toLowerCase().replace("\n", ", ");
    for (Point poi : points) {
      if (poi.getLongitude() != 0 && poi.getLatitude() != 0 && info.startsWith(poi.getInfo().toLowerCase())) {
        p.setLongitude(poi.getLongitude());
        p.setLatitude(poi.getLatitude());
        return true;
      }
    }
    return false;
  }

}
