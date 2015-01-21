package at.wrk.coceso.service.point;

import at.wrk.coceso.service.csv.CsvParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class CsvAutocomplete implements IAutocomplete {

  private final static Logger LOG = Logger.getLogger(CsvAutocomplete.class);
  private final TreeMap<String, String> streets;

  public CsvAutocomplete() {
    streets = new TreeMap<>();
  }

  protected void init(Resource source, Charset charset, char delimiter) throws CsvParseException, IOException {
    LOG.info(String.format("Loading CSV from %s", source.getDescription()));
    String csv = StreamUtils.copyToString(source.getInputStream(), charset);
    Iterable<CSVRecord> records;
    try {
      records = CSVParser.parse(csv, CSVFormat.RFC4180.withDelimiter(delimiter).withHeader());
    } catch (IOException e) {
      throw new CsvParseException("Couldn't parse street name CSV");
    }
    for (CSVRecord record : records) {
      streets.putAll(parseRecord(record));
    }
    LOG.info(String.format("Fully loaded street names: %d entries", streets.size()));
  }

  protected abstract Map<String, String> parseRecord(CSVRecord record);

  @Override
  public List<String> getAll(String filter, Integer max) {
    String to = filter.substring(0, filter.length() - 1) + (char) (filter.charAt(filter.length() - 1) + 1);
    List<String> filtered = new LinkedList<>(streets.subMap(filter, to).values());

    if (max == null || max > filtered.size()) {
      filtered.addAll(getContaining(filter, max == null ? null : max - filtered.size()));
    }
    return filtered;
  }

  @Override
  public List<String> getContaining(String filter, Integer max) {
    List<String> filtered = new LinkedList<>();
    for (Map.Entry<String, String> entry : streets.entrySet()) {
      if (max != null && filtered.size() >= max) {
        break;
      }
      if (entry.getKey().indexOf(filter) > 0) {
        filtered.add(entry.getValue());
      }
    }
    return filtered;
  }

}
