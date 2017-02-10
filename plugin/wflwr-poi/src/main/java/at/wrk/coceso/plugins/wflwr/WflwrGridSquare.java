package at.wrk.coceso.plugins.wflwr;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.GridSquarePoi;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(21)
public class WflwrGridSquare extends GridSquarePoi {

  public WflwrGridSquare() {
    super("WFLWR/Resselpark/", new GridSquare(48.201110, 16.366190, 'A', 1), new LatLng(0, 3.35857E-4), new LatLng(-2.24128E-4, 0), 'Z', 17);
  }

}
