package at.wrk.coceso.service;

import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.service.csv.CsvParseException;
import at.wrk.coceso.service.point.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.web.client.RestTemplate;

@Service
public class PointService {

  private final static Logger LOG = Logger.getLogger(PointService.class);

  private IAutocomplete autocomplete;
  private ILocate locate;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private PointDao pointDao;

  @PostConstruct
  public void init() {
    try {
      JsonPoi jsonPoi = new JsonPoi(new ClassPathResource("poi.json"));
      autocomplete = new MultipleAutocomplete(jsonPoi, new ViennaAutocomplete());
      locate = new MultipleLocate(jsonPoi, new ViennaLocate(restTemplate));
    } catch (IOException | CsvParseException ex) {
      LOG.error(ex);
    }
  }

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
    return autocomplete.getAll(filter.toLowerCase(), 20);
  }

  private boolean locate(Point p) {
    if (locate == null || Point.isEmpty(p) || (p.getLatitude() != 0 && p.getLongitude() != 0)) {
      return false;
    }
    return locate.locate(p);
  }
}
