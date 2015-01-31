package at.wrk.coceso.service.point;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public abstract class CsvAutocomplete extends PreloadedAutocomplete {

  private final static Logger LOG = Logger.getLogger(CsvAutocomplete.class);

  protected void init(Resource source, Charset charset, char delimiter) {
    LOG.info(String.format("Loading CSV from %s", source.getDescription()));
    try {
      String csv = StreamUtils.copyToString(source.getInputStream(), charset);
      Iterable<CSVRecord> records = CSVParser.parse(csv, CSVFormat.RFC4180.withDelimiter(delimiter).withHeader());
      for (CSVRecord record : records) {
        parseRecord(record);
      }
      LOG.info(String.format("Fully loaded street names: %d entries", autocomplete.size()));
    } catch (IOException e) {
      LOG.error("Couldn't parse street name CSV");
    }
  }

  protected abstract void parseRecord(CSVRecord record);

}
