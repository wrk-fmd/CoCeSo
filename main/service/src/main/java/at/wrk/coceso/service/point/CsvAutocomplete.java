package at.wrk.coceso.service.point;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

public abstract class CsvAutocomplete extends PreloadedAutocomplete {

  private final static Logger LOG = LoggerFactory.getLogger(CsvAutocomplete.class);

  public CsvAutocomplete(char delimiter, String data, Function<CSVRecord, Map<String, String>> parse) throws IOException {
    super(StreamSupport.stream(CSVParser.parse(data, CSVFormat.RFC4180.withDelimiter(delimiter).withHeader()).spliterator(), false)
        .flatMap(parse.andThen(Map::entrySet).andThen(Set::stream))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new)));
    LOG.info("Fully loaded street names: {} entries", autocomplete.size());
  }

  public CsvAutocomplete(Charset charset, char delimiter, Resource source, Function<CSVRecord, Map<String, String>> parse) throws IOException {
    this(delimiter, StreamUtils.copyToString(source.getInputStream(), charset), parse);
  }

}
