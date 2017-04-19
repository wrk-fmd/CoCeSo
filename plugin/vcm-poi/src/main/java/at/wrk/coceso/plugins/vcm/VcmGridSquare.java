package at.wrk.coceso.plugins.vcm;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.GridSquarePoi;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class VcmGridSquare extends GridSquarePoi {

  public VcmGridSquare() {
//    super("VCM/Heldenplatz/", new LatLng(48.206114, 16.360507, 'C', 10), new LatLng(1.80011E-4, 2.98551E-4), new LatLng(-1.99578E-4, 2.69281E-4), 'R', 18);
//    super("VCM/Rathaus/", new GridSquare(48.208467, 16.354896, 'A', 1), new LatLng(2.65001E-4, 7.93422E-5), new LatLng(-5.41714E-5, 3.90278E-4), 'X', 17);
    super("VCM/Umfeld/", new GridSquare(48.207930, 16.354709, 'A', 1), new LatLng(2.66214E-4, 7.93787E-5), new LatLng(-5.35487E-5, 3.94626E-4), 'Z', 18);
  }

}
