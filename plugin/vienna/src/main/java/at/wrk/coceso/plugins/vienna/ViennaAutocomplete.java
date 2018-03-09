package at.wrk.coceso.plugins.vienna;

import at.wrk.geocode.autocomplete.CsvAutocomplete;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class ViennaAutocomplete extends CsvAutocomplete<String> {

  private final static String URL_CACHE = "streetnames.csv";
  private final static String URL_ORIGINAL = "http://data.wien.gv.at/daten/geo?service=WFS&amp;version=1.1.0&amp;request=GetFeature"
      + "&amp;typeName=ogdwien:GEONAMENSVERZOGD&amp;propertyName=STR_NAME,BEZLISTE&amp;outputFormat=csv";

  private final static Logger LOG = LoggerFactory.getLogger(ViennaAutocomplete.class);

  public ViennaAutocomplete() throws IOException {
    this(true);
  }

  public ViennaAutocomplete(boolean useCache) throws IOException {
    super(Charset.forName("ISO-8859-1"), ',', useCache ? new ClassPathResource(URL_CACHE) : new UrlResource(URL_ORIGINAL), ViennaAutocomplete::parseRecord, Function.identity());
  }

  @Override
  public String getString(String value) {
    return value;
  }

  private static Stream<String> parseRecord(CSVRecord record) {
    String street = record.get("STR_NAME").trim(),
        districts = record.get("BEZLISTE").trim();

    if (districts.length() == 0) {
      return Stream.of(street);
    }

    return Arrays.stream(districts.split("\\|")).map(d -> {
      try {
        return 1000 + Integer.parseInt(d) * 10;
      } catch (NumberFormatException e) {
        LOG.warn("Error processing district '{}' for {}", d, street);
        return null;
      }
    }).filter(Objects::nonNull).map(code -> street + "\n" + code + " Wien");
  }

}
