package at.wrk.geocode.poi;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.ReverseResult;
import at.wrk.geocode.autocomplete.AutocompleteSupplier;
import at.wrk.geocode.autocomplete.PreloadedAutocomplete;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a POI for every grid
 */
public abstract class GridSquarePoi extends PreloadedAutocomplete<Poi> implements PoiSupplier {

  private final String name;
  private final GridSquare start;
  private final LatLng xStep, yStep;
  private final char xMax;
  private final int yMax;

  /**
   * Create grid squares
   *
   * @param name Base name for the grid squares
   * @param start Start of the squares (coordinates, character and name)
   * @param xStep Degree difference in x direction (advancing in character)
   * @param yStep Degree difference in y direction (advancing in number)
   * @param xMax Maximum square in x direction (highest character)
   * @param yMax Maximum square in y direction (highest number)
   */
  public GridSquarePoi(String name, GridSquare start, LatLng xStep, LatLng yStep, char xMax, int yMax) {
    this.name = name.toLowerCase();
    this.start = start;
    this.xStep = xStep;
    this.yStep = yStep;
    this.xMax = xMax;
    this.yMax = yMax;

    for (char x = start.x; x <= xMax; x++) {
      for (int y = start.y; y <= yMax; y++) {
        String val = name + x + y;
        values.put(AutocompleteSupplier.getKey(val), new PoiImpl(val, start.calculate(x, xStep, y, yStep)));
      }
    }
  }

  @Override
  public String getString(Poi value) {
    return value.getText();
  }

  @Override
  public Poi getPoi(String text) {
    if (text.length() < name.length() + 2 || !text.startsWith(name)) {
      return null;
    }

    char x = text.charAt(name.length());
    if (!Character.isAlphabetic(x)) {
      return null;
    }

    x = Character.isLowerCase(start.x) ? Character.toLowerCase(x) : Character.toUpperCase(x);
    if (x < start.x || x > xMax) {
      return null;
    }

    int y;
    try {
      Matcher matcher = Pattern.compile("\\d+").matcher(text.substring(name.length() + 1));
      matcher.find();
      y = Integer.parseInt(matcher.group());
    } catch (NumberFormatException | IllegalStateException e) {
      return null;
    }
    if (y < start.y || y > yMax) {
      return null;
    }

    return new PoiImpl(name + x + y, start.calculate(x, xStep, y, yStep));
  }


  @Override
  public LatLng geocode(Poi poi) {
    Poi foundPoi = getPoi(poi.getText());
    return foundPoi == null ? null : foundPoi.getCoordinates();
  }

  @Override
  public ReverseResult<Poi> reverse(LatLng coordinates) {
    double dLat = coordinates.getLat() - start.getLat(),
        dLng = coordinates.getLng() - start.getLng(),
        a = xStep.getLat() * yStep.getLng() - xStep.getLng() * yStep.getLat();

    int dx = (int) Math.round((dLat * yStep.getLng() - dLng * yStep.getLat()) / a),
        dy = (int) Math.round((dLng * xStep.getLat() - dLat * xStep.getLng()) / a);

    if (dx < 0 || dx > xMax - start.x || dy < 0 || dy > yMax - start.y) {
      return null;
    }

    char x = (char) (start.x + dx);
    int y = start.y + dy;
    LatLng calcCoord = start.calculate(x, xStep, y, yStep);
    return new ReverseResult<>(coordinates.distance(calcCoord), new PoiImpl(name + x + y, calcCoord), calcCoord);
  }

  protected static class GridSquare extends LatLng {

    public final char x;
    private final int y;

    public GridSquare(double lat, double lng, char x, int y) {
      super(lat, lng);
      this.x = x;
      this.y = y;
    }

    /**
     * Calculate the coordinates for the specified grid square
     *
     * @param x The x value for the grid square
     * @param xStep The x width of each square, given in difference of degrees
     * @param y The y value for the grid square
     * @param yStep The y height of each square, given in difference of degrees
     * @return The coordinates for the specified square
     */
    public LatLng calculate(char x, LatLng xStep, int y, LatLng yStep) {
      int dx = x - this.x, dy = y - this.y;
      return new LatLng(getLat() + dx * xStep.getLat() + dy * yStep.getLat(), getLng() + dx * xStep.getLng() + dy * yStep.getLng());
    }

  }

}
