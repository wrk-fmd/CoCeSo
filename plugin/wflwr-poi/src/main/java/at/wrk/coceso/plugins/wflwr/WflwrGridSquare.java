package at.wrk.coceso.plugins.wflwr;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.GridSquarePoi;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(21)
public class WflwrGridSquare extends GridSquarePoi {

  public WflwrGridSquare() {
    //super("WFLWR/Resselpark/", new GridSquare(48.201110, 16.366190, 'A', 1), new LatLng(0, 3.35857E-4), new LatLng(-2.24128E-4, 0), 'Z', 17);
    //super("WFLWR/Start/", new GridSquare(48.206717, 16.359191, 'A', 1), new LatLng(2.02280e-04, -1.23044e-04), new LatLng(8.21927e-05, 3.02818e-04), 'Z', 17);
    super("WFLWR/Rathaus/", new GridSquare(48.206243, 16.359009, 'A', 1), new LatLng(2.19486e-04, -1.35865e-04), new LatLng(9.19668e-05, 3.24254e-04), 'Y', 17);
  }

}
