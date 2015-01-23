package at.wrk.coceso.service;

import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.service.point.IAutocomplete;
import at.wrk.coceso.service.point.ILocate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

  @Autowired
  private IAutocomplete autocomplete;

  @Autowired
  private ILocate locate;

  @Autowired
  private PointDao pointDao;

  public Point createIfNotExists(Point dummy) {
    if (dummy == null) {
      return null;
    }

    // Marker for deletion
    if (dummy.getId() == -2) {
      return dummy;
    }

    // If the id already exists, return Point from Database
    if (dummy.getId() > 0) {
      Point p = pointDao.getById(dummy.getId());
      if (p != null) {
        if (locate(p)) {
          pointDao.update(p);
        }
        return p;
      }
    }

    Point point = pointDao.getByInfo(dummy.getInfo());
    if (point == null && dummy.getInfo() != null && !dummy.getInfo().isEmpty()) {
      locate(dummy);
      dummy.setId(pointDao.add(dummy));
      return dummy;
    } else {
      if (locate(point)) {
        pointDao.update(point);
      }
      return point;
    }
  }

  public Point getById(int id) {
    return pointDao.getById(id);
  }

  public List<String> autocomplete(String filter) {
    if (autocomplete == null || filter == null || filter.length() <= 1) {
      return null;
    }
    return autocomplete.getAll(filter.toLowerCase().replaceAll("\n", ", "), 20);
  }

  private boolean locate(Point p) {
    if (locate == null || Point.isEmpty(p) || (p.getLatitude() != 0 && p.getLongitude() != 0)) {
      return false;
    }
    return locate.locate(p);
  }
}
