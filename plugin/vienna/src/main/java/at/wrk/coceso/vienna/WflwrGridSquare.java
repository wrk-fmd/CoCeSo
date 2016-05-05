package at.wrk.coceso.vienna;

import at.wrk.coceso.service.point.GridSquare;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(21)
public class WflwrGridSquare extends GridSquare {

  public WflwrGridSquare() {
    super("WFLWR/Resselpark/", 48.201110, 16.366190, 0, 3.35857E-4, -2.24128E-4, 0, 'A', 'Z', 1, 17);
  }

}
