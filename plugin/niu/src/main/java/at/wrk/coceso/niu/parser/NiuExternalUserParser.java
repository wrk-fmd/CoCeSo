package at.wrk.coceso.niu.parser;

import at.wrk.coceso.importer.ImportException;
import at.wrk.coceso.niu.data.ExternalUser;
import at.wrk.coceso.niu.data.ExternalUserId;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class NiuExternalUserParser implements ExternalUserParser {
    private static final Logger LOG = LoggerFactory.getLogger(NiuExternalUserParser.class);

    private static final char CSV_DELIMITER = '|';

    private static final String LASTNAME = "Nachname";
    private static final String FIRSTNAME = "Vorname";
    private static final String PERSONNELID = "DNr.";
    private static final Set<String> TELEPHONE_FIELDS = Set.of(
            "Telefon geschäftlich",
            "Telefon privat",
            "Telefon WRK",
            "Handy privat",
            "Handy geschäftlich",
            "Handy WRK"
    );

    private final CSVFormat csvFormat;

    public NiuExternalUserParser() {
        csvFormat = CSVFormat.RFC4180
                .withDelimiter(CSV_DELIMITER)
                .withHeader()
                .withAllowMissingColumnNames();
    }

    @Override
    public Collection<ExternalUser> parseExternalUsers(final String csvData) throws ImportException {
        Iterable<CSVRecord> records;
        try {
            records = CSVParser.parse(csvData, csvFormat);
        } catch (IOException e) {
            throw new ImportException("Could not parse users CSV file", e);
        }

        return StreamSupport.stream(records.spliterator(), false)
                .map(NiuExternalUserParser::getExternalUserFromCsvRecord)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }

    private static ExternalUser getExternalUserFromCsvRecord(final CSVRecord record) throws ImportException {
        ExternalUserId externalUserId = getExternalUserId(record);

        ExternalUser externalUser = null;
        if (externalUserId != null) {
            Set<String> telephoneNumbers = TELEPHONE_FIELDS
                    .stream()
                    .map(fieldName -> getOrNull(record, fieldName))
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());

            externalUser = new ExternalUser(externalUserId, telephoneNumbers);
        }

        return externalUser;
    }

    @Nullable
    private static String getOrNull(final CSVRecord record, final String fieldName) {
        String value = null;
        try {
            value = record.get(fieldName);
        } catch (IllegalArgumentException e) {
            LOG.warn("Invalid column name: {}. Was the file uploaded with the correct encoding? (UTF-8)", fieldName);
        }

        return value;
    }

    @Nullable
    private static ExternalUserId getExternalUserId(final CSVRecord record) throws ImportException {
        String personnelIdString;
        String lastname;
        String firstname;
        try {
            personnelIdString = record.get(PERSONNELID);
            lastname = record.get(LASTNAME);
            firstname = record.get(FIRSTNAME);
        } catch (IllegalArgumentException e) {
            throw new ImportException("Required field is not present in CSV.", e);
        }

        Integer personnelId = null;
        try {
            personnelId = Integer.parseInt(personnelIdString);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid personnel ID while parsing: {}", personnelIdString);
        }

        ExternalUserId externalUserId = null;
        if (personnelId != null && lastname != null && firstname != null) {
            externalUserId = new ExternalUserId(personnelId, lastname, firstname);
        }

        return externalUserId;
    }
}
