package at.wrk.coceso.vienna;

import at.wrk.coceso.service.point.GridSquare;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//@Component
@Order(20)
public class VcmGridSquare extends GridSquare {

  public VcmGridSquare() {
//    super("VCM/Heldenplatz/", 48.206114, 16.360507, 1.80011E-4, 2.98551E-4, -1.99578E-4, 2.69281E-4, 'C', 'R', 10, 18);
    super("VCM/Rathaus/", 48.208467, 16.354896, 2.65001E-4, 7.93422E-5, -5.41714E-5, 3.90278E-4, 'A', 'X', 1, 17);
  }

}
