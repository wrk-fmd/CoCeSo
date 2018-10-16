package at.wrk.coceso.plugins.vienna.util;

import at.wrk.geocode.autocomplete.CSVRecordParser;
import com.google.common.collect.ImmutableList;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ViennaStreetParser implements CSVRecordParser<String> {
    private static final Logger LOG = LoggerFactory.getLogger(ViennaStreetParser.class);
    private static final String STREET_NAME_COLUMN_NAME = "STR_NAME";
    private static final String DISTRICT_COLUMN_NAME = "BEZLISTE";

    private final PostalCodeUtil postalCodeUtil;

    @Autowired
    public ViennaStreetParser(final PostalCodeUtil postalCodeUtil) {
        this.postalCodeUtil = postalCodeUtil;
    }

    @Override
    public List<String> parseCsvRecord(CSVRecord record) {
        final String street = record.get(STREET_NAME_COLUMN_NAME).trim();
        final String districts = record.get(DISTRICT_COLUMN_NAME).trim();

        if (districts.length() == 0) {
            return ImmutableList.of(street);
        }

        return Arrays.stream(districts.split("\\|"))
                .map(postalCodeUtil::createPostalCodeForDistrictNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(code -> street + "\n" + code + " Wien")
                .collect(Collectors.toList());
    }
}
