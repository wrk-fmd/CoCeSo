package at.wrk.coceso.service;

import at.wrk.coceso.entity.Point;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PointService {

  Point createIfNotExists(Point dummy);

  Collection<String> autocomplete(String filter);

}
