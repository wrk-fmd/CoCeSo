package at.wrk.geocode.autocomplete;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

/**
 * A pre-loaded autocomplete solution loading its data from CSV
 *
 * @param <T>
 */
public abstract class CsvAutocomplete<T> extends PreloadedAutocomplete<T> {

  private final static Logger LOG = LoggerFactory.getLogger(CsvAutocomplete.class);

  /**
   * Load entries from a CSV string
   *
   * @param delimiter The column delimiter
   * @param data The CSV data
   * @param parse Function to parse each CSV record to entries
   * @param getString Function to get the String used as key from each entry
   * @throws IOException on CSV parsing failure
   */
  public CsvAutocomplete(char delimiter, String data, Function<CSVRecord, Stream<T>> parse, Function<T, String> getString) throws IOException {
    super(
        StreamSupport.stream(CSVParser.parse(data, CSVFormat.RFC4180.withDelimiter(delimiter).withHeader()).spliterator(), false)
        .flatMap(parse).collect(Collectors.toMap(getString.andThen(AutocompleteSupplier::getKey), Function.identity(), (a, b) -> a, TreeMap::new))
    );
    LOG.info("Fully loaded street names: {} entries", values.size());
  }

  /**
   * Load entries from a resource containing CSV data
   *
   * @param charset The charset used for parsing the resource
   * @param delimiter The column delimiter
   * @param source The CSV resource
   * @param parse Function to parse each CSV record to entries
   * @param getString Function to get the String used as key from each entry
   * @throws IOException on error reading the resource or CSV parsing failure
   */
  public CsvAutocomplete(Charset charset, char delimiter, Resource source, Function<CSVRecord, Stream<T>> parse, Function<T, String> getString) throws IOException {
    this(delimiter, StreamUtils.copyToString(source.getInputStream(), charset), parse, getString);
  }

}
