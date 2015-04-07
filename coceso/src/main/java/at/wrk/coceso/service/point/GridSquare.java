package at.wrk.coceso.service.point;

import at.wrk.coceso.entity.Point;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridSquare extends PreloadedAutocomplete implements ILocate {

  private final String name;
  private final double lat, lng, xLat, xLng, yLat, yLng;
  private final char x1, x2;
  private final int y1, y2;

  public GridSquare(String name, double lat, double lng, double xLat, double xLng, double yLat, double yLng, char x1, char x2, int y1, int y2) {
    this.name = name.toLowerCase();
    this.lat = lat;
    this.lng = lng;
    this.xLat = xLat;
    this.xLng = xLng;
    this.yLat = yLat;
    this.yLng = yLng;
    this.x1 = Character.toLowerCase(x1);
    this.x2 = Character.toLowerCase(x2);
    this.y1 = y1;
    this.y2 = y2;

    for (char x = x1; x <= x2; x++) {
      for (int y = y1; y <= y2; y++) {
        String val = name + x + y;
        autocomplete.put(val.toLowerCase().replaceAll("\n", ", "), val);
      }
    }
  }

  @Override
  public boolean locate(Point p) {
    String info = p.getInfo().toLowerCase();
    if (info.length() < name.length() + 2 || !info.startsWith(name)) {
      return false;
    }

    char x = info.charAt(name.length());
    if (x < x1 || x > x2) {
      return false;
    }

    int y;
    try {
      Matcher matcher = Pattern.compile("\\d+").matcher(info.substring(name.length() + 1));
      matcher.find();
      y = Integer.parseInt(matcher.group());
    } catch (NumberFormatException | IllegalStateException e) {
      return false;
    }
    if (y < y1 || y > y2) {
      return false;
    }

    p.setLatitude(lat + (x - x1) * xLat + (y - y1) * yLat);
    p.setLongitude(lng + (x - x1) * xLng + (y - y1) * yLng);
    return true;
  }

}
