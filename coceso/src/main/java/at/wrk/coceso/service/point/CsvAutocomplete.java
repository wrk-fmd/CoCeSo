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

public abstract class CsvAutocomplete extends PreloadedAutocomplete {

  private final static Logger LOG = Logger.getLogger(CsvAutocomplete.class);

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
      parseRecord(record);
    }
    LOG.info(String.format("Fully loaded street names: %d entries", autocomplete.size()));
  }

  protected abstract void parseRecord(CSVRecord record);

}
