package at.wrk.coceso.plugins.vcm;

import at.wrk.geocode.LatLng;
import at.wrk.geocode.poi.GridSquarePoi;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class VcmGridSquare extends GridSquarePoi {

    public VcmGridSquare() {
        super("VCM/Umfeld/",
                new GridSquare(48.207930, 16.354709, 'A', 1),
                new LatLng(2.66214E-4, 7.93787E-5),
                new LatLng(-5.35487E-5, 3.94626E-4),
                'Z',
                18);
    }
}
