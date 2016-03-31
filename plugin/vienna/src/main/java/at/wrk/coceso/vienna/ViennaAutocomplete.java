package at.wrk.coceso.vienna;

import at.wrk.coceso.service.point.CsvAutocomplete;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class ViennaAutocomplete extends CsvAutocomplete {

  private final static String cache = "streetnames.csv";
  private final static String original = "http://data.wien.gv.at/daten/geo?service=WFS&amp;version=1.1.0&amp;request=GetFeature"
      + "&amp;typeName=ogdwien:GEONAMENSVERZOGD&amp;propertyName=STR_NAME,BEZLISTE&amp;outputFormat=csv";

  private final static Logger LOG = LoggerFactory.getLogger(ViennaAutocomplete.class);

  public ViennaAutocomplete() throws IOException {
    this(true);
  }

  public ViennaAutocomplete(boolean useCache) throws IOException {
    super(Charset.forName("ISO-8859-1"), ',', useCache ? new ClassPathResource(cache) : new UrlResource(original), ViennaAutocomplete::parseRecord);
  }

  private static Map<String, String> parseRecord(CSVRecord record) {
    String street = record.get("STR_NAME").trim(),
        districts = record.get("BEZLISTE").trim(),
        key = street.toLowerCase();

    if (districts.length() == 0) {
      return Collections.singletonMap(key, street);
    }

    return Arrays.stream(districts.split("\\|")).map(d -> {
      try {
        return 1000 + Integer.parseInt(d) * 10;
      } catch (NumberFormatException e) {
        LOG.warn("Error processing district '{}' for {}", d, street);
        return null;
      }
    }).filter(Objects::nonNull).collect(Collectors.toMap(
        code -> key + ", " + code + " wien",
        code -> street + "\n" + code + " Wien")
    );
  }

}
