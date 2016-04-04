package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Point;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PointService {

  Point createIfNotExists(Point dummy);

  Point createIfNotExists(Point dummy, Concern concern);

  Collection<String> autocomplete(String filter);

  Collection<String> autocomplete(String filter, Concern concern);

}
