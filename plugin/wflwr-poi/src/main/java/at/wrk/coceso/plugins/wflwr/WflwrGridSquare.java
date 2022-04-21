package at.wrk.coceso.plugins.wflwr;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.GridSquarePoi;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(21)
public class WflwrGridSquare extends GridSquarePoi {

    public WflwrGridSquare() {
        super("WFLWR/Rathaus/",
                new GridSquare(48.206243, 16.359009, 'A', 1),
                new LatLng(2.19486e-04, -1.35865e-04),
                new LatLng(9.19668e-05, 3.24254e-04),
                'Y',
                17);
    }
}
