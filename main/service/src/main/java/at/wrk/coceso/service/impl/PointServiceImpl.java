package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
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
    return createIfNotExists(dummy, null);
  }

  @Override
  public Point createIfNotExists(Point dummy, Concern concern) {
    if (dummy == null) {
      return null;
    }

    if (dummy.getId() != null) {
      // If the id already exists, return Point from Database
      Point p = pointRepository.findOne(dummy.getId());
      if (p != null) {
        return locate(p, concern);
      }
      dummy.setId(null);
    }

    dummy.setInfo(StringUtils.trimToNull(dummy.getInfo()));
    if (dummy.getInfo() != null) {
      Point p = pointRepository.findByInfo(dummy.getInfo());
      if (p != null) {
        return locate(p, concern);
      }

      dummy.setLatitude(null);
      dummy.setLongitude(null);
      return locate(pointRepository.save(dummy), concern);
    }

    if (dummy.getLatitude() != null && dummy.getLongitude() != null) {
      return pointRepository.save(dummy);
    }

    return null;
  }

  @Override
  public Collection<String> autocomplete(String filter) {
    return autocomplete(filter, null);
  }

  @Override
  public Collection<String> autocomplete(String filter, Concern concern) {
    if (filter == null || filter.length() <= 1) {
      return null;
    }
    return autocomplete.getAll(filter.toLowerCase().replaceAll("\n", ", "), 20, concern);
  }

  private Point locate(Point p, Concern concern) {
    if (locate == null || StringUtils.isEmpty(p.getInfo()) || (p.getLatitude() != null && p.getLongitude() != null)) {
      return p;
    }
    if (locate.locate(p, concern)) {
      p = pointRepository.save(p);
    }
    return p;
  }
}
