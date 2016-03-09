package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Point;
import at.wrk.coceso.repository.PointRepository;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.point.MultipleAutocomplete;
import at.wrk.coceso.service.point.MultipleLocate;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class PointServiceImpl implements PointService {

  @Autowired
  private MultipleAutocomplete autocomplete;

  @Autowired
  private MultipleLocate locate;

  @Autowired
  private PointRepository pointRepository;

  @Override
  public Point createIfNotExists(Point dummy) {
    if (dummy == null) {
      return null;
    }

    if (dummy.getId() != null) {
      // If the id already exists, return Point from Database
      Point p = pointRepository.findOne(dummy.getId());
      if (p != null) {
        return locate(p);
      }
      dummy.setId(null);
    }

    dummy.setInfo(StringUtils.trimToNull(dummy.getInfo()));
    if (dummy.getInfo() != null) {
      Point p = pointRepository.findByInfo(dummy.getInfo());
      if (p != null) {
        return locate(p);
      }

      dummy.setLatitude(null);
      dummy.setLongitude(null);
      return locate(pointRepository.save(dummy));
    }

    if (dummy.getLatitude() != null && dummy.getLongitude() != null) {
      return pointRepository.save(dummy);
    }

    return null;
  }

  @Override
  public Collection<String> autocomplete(String filter) {
    if (filter == null || filter.length() <= 1) {
      return null;
    }
    return autocomplete.getAll(filter.toLowerCase().replaceAll("\n", ", "), 20);
  }

  private Point locate(Point p) {
    if (locate == null || StringUtils.isEmpty(p.getInfo()) || (p.getLatitude() != null && p.getLongitude() != null)) {
      return p;
    }
    if (locate.locate(p)) {
      p = pointRepository.save(p);
    }
    return p;
  }
}
