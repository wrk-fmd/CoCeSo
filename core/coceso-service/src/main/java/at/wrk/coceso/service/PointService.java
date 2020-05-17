package at.wrk.coceso.service;

import at.wrk.coceso.dto.point.PointDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.point.Point;

import java.util.Collection;

public interface PointService {

    Collection<String> autocomplete(String filter, Concern concern);

    Point getPoint(Concern concern, PointDto data);
}
