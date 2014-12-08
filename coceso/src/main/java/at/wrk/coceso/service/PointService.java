package at.wrk.coceso.service;

import at.wrk.coceso.dao.PointDao;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.service.csv.CsvParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PointService {

  private final static Logger LOG = Logger.getLogger(PointService.class);

  private static final SourceConfig streetConfig = new SourceConfig(
          "http://data.wien.gv.at/daten/geo?service=WFS&version=1.1.0&request=GetFeature"
          + "&typeName=ogdwien:GEONAMENSVERZOGD&propertyName=STR_NAME,BEZLISTE&outputFormat=csv",
          Charset.forName("ISO-8859-1"), ',', "STR_NAME", "BEZLISTE"
  );

  private static TreeMap<String, String> poiList = null;

  @Autowired
  private ApplicationContext appContext;

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
        return p;
      }
    }

    Point point = pointDao.getByInfo(dummy.getInfo());
    if (point == null && dummy.getInfo() != null && !dummy.getInfo().isEmpty()) {
      dummy.setId(pointDao.add(dummy));
      return dummy;
    } else {
      return point;
    }
  }

  public Point getById(int id) {
    return pointDao.getById(id);
  }

  public List<String> autocomplete(String address) {
    return filterCollection(getPoiList(), address, 20);
  }

  private TreeMap<String, String> getPoiList() {
    if (poiList == null) {
      try {
        poiList = parseStreetList(getCsvBody(streetConfig.url));
      } catch (IOException | CsvParseException ex) {
        LOG.error(ex);
      }
    }
    return poiList;
  }

  private String getCsvBody(String url) throws IOException {
    LOG.info(String.format("Loading CSV from %s", url));
    Resource resource = appContext.getResource(url);
    return StreamUtils.copyToString(resource.getInputStream(), streetConfig.charset);
  }

  private TreeMap<String, String> parseStreetList(String csvBody) throws CsvParseException {
    TreeMap<String, String> addresses = new TreeMap<>();

    Iterable<CSVRecord> records;
    try {
      records = CSVParser.parse(csvBody, CSVFormat.RFC4180.withDelimiter(streetConfig.delimiter).withHeader());
    } catch (IOException e) {
      throw new CsvParseException("Couldn't parse street name CSV");
    }

    for (CSVRecord record : records) {
      String street = record.get(streetConfig.streetField).trim(),
              districts = record.get(streetConfig.districtField).trim(),
              key = street.toLowerCase();
      if (districts.length() > 0) {
        for (String district : (districts.split("\\|"))) {
          try {
            int code = 1000 + Integer.parseInt(district) * 10;
            addresses.put(key + ", " + code + " wien", street + "\n" + code + " Wien");
          } catch (NumberFormatException e) {
            LOG.warn(String.format("Error processing district '%s' for %s", new Object[]{district, street}));
          }
        }
      } else {
        addresses.put(key, street);
      }
    }

    LOG.info(String.format("Fully loaded street names: %d entries", addresses.size()));
    return addresses;
  }

  private List<String> filterCollection(TreeMap<String, String> unfiltered, String filter, int max) {
    if (filter.length() <= 1 || unfiltered == null) {
      return null;
    }

    String from = filter.toLowerCase();
    String to = from.substring(0, from.length() - 1) + (char) (from.charAt(from.length() - 1) + 1);
    List<String> filtered = new LinkedList<>(unfiltered.subMap(from, to).values());

    if (filtered.size() < max) {
      for (Map.Entry<String, String> entry : unfiltered.entrySet()) {
        if (entry.getKey().indexOf(filter) > 0) {
          filtered.add(entry.getValue());
          if (filtered.size() >= max) {
            break;
          }
        }
      }
    }
    return filtered;
  }

  private static class SourceConfig {

    private String url;
    private Charset charset;
    private char delimiter;
    private String streetField;
    private String districtField;

    public SourceConfig(String url, Charset charset, char delimiter, String streetField, String districtField) {
      this.url = url;
      this.charset = charset;
      this.delimiter = delimiter;
      this.streetField = streetField;
      this.districtField = districtField;
    }
  }

}
