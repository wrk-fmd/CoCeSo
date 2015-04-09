package at.wrk.coceso.service.point;

import java.nio.charset.Charset;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

public class ViennaAutocomplete extends CsvAutocomplete {

  private final static Logger LOG = Logger.getLogger(ViennaAutocomplete.class);

  public ViennaAutocomplete(Resource source) {
    // Source shoult point to
    // http://data.wien.gv.at/daten/geo?service=WFS&version=1.1.0&request=GetFeature&typeName=ogdwien:GEONAMENSVERZOGD&propertyName=STR_NAME,BEZLISTE&outputFormat=csv
    // or local cache
    init(source, Charset.forName("ISO-8859-1"), ',');
  }

  @Override
  protected void parseRecord(CSVRecord record) {
    String street = record.get("STR_NAME").trim(),
        districts = record.get("BEZLISTE").trim(),
        key = street.toLowerCase();
    if (districts.length() > 0) {
      for (String district : (districts.split("\\|"))) {
        try {
          int code = 1000 + Integer.parseInt(district) * 10;
          autocomplete.put(key + ", " + code + " wien", street + "\n" + code + " Wien");
        } catch (NumberFormatException e) {
          LOG.warn(String.format("Error processing district '%s' for %s", new Object[]{district, street}));
        }
      }
    } else {
      autocomplete.put(key, street);
    }
  }

}
