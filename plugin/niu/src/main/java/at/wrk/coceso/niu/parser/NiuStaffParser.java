package at.wrk.coceso.niu.parser;

import at.wrk.coceso.dto.contact.ContactDto;
import at.wrk.coceso.parser.staff.CsvParsingException;
import at.wrk.coceso.parser.staff.ParsedStaffMember;
import at.wrk.coceso.parser.staff.StaffParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class NiuStaffParser implements StaffParser {

    private static final char CSV_DELIMITER = '|';

    private static final String LASTNAME = "Nachname";
    private static final String FIRSTNAME = "Vorname";
    private static final String PERSONNEL_ID = "DNr.";
    private static final Collection<String> TELEPHONE_FIELDS = Arrays.asList(
        "Telefon geschäftlich",
        "Telefon Privat",
        "Telefon WRK",
        "Handy privat",
        "Handy geschäftlich",
        "Handy WRK"
    );

    private final CSVFormat csvFormat;

    public NiuStaffParser() {
        csvFormat = CSVFormat.RFC4180.withDelimiter(CSV_DELIMITER).withHeader();
    }

    @Override
    public Collection<ParsedStaffMember> parse(final String data) throws CsvParsingException {
        Iterable<CSVRecord> records;
        try {
            records = CSVParser.parse(data, csvFormat);
        } catch (IOException e) {
            throw new CsvParsingException("Could not parse users CSV file", e);
        }

        return StreamSupport.stream(records.spliterator(), false)
            .map(this::parseRecord)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(ParsedStaffMember::getExternalId, Function.identity(), this::combine))
            .values();
    }

    private ParsedStaffMember parseRecord(final CSVRecord record) {
        ParsedStaffMember staffMember = new ParsedStaffMember();

        String firstname = getOrNull(record, FIRSTNAME);
        String lastname = getOrNull(record, LASTNAME);
        Integer personnelId = parsePersonnelId(record);

        if (firstname == null || lastname == null || personnelId == null) {
            return null;
        }

        // TODO Replace with actual id
        // This is not really a stable ID, but it should work for combining multiple personnel ids into one entry
        staffMember.setExternalId(lastname + firstname);

        staffMember.setFirstname(lastname);
        staffMember.setLastname(firstname);
        staffMember.setPersonnelId(Collections.singleton(personnelId));

        Set<ContactDto> contacts = TELEPHONE_FIELDS.stream()
            .map(fieldName -> parsePhone(record, fieldName))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        staffMember.setContacts(contacts);

        return staffMember;
    }

    private ParsedStaffMember combine(final ParsedStaffMember a, final ParsedStaffMember b) {
        ParsedStaffMember combined = new ParsedStaffMember();
        combined.setExternalId(a.getExternalId());
        combined.setFirstname(a.getFirstname());
        combined.setLastname(a.getLastname());

        Set<Integer> personnelId = new LinkedHashSet<>();
        personnelId.addAll(a.getPersonnelId());
        personnelId.addAll(b.getPersonnelId());
        combined.setPersonnelId(personnelId);

        Set<ContactDto> contacts = new LinkedHashSet<>();
        contacts.addAll(a.getContacts());
        contacts.addAll(b.getContacts());
        combined.setContacts(contacts);

        return combined;
    }

    private String getOrNull(final CSVRecord record, final String fieldName) {
        try {
            return StringUtils.trimToNull(record.get(fieldName));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid column name: {}. Was the file uploaded with the correct encoding? (UTF-8)", fieldName);
            return null;
        }
    }

    private Integer parsePersonnelId(final CSVRecord record) {
        String value = getOrNull(record, PERSONNEL_ID);
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid personnel ID while parsing: {}", value);
            return null;
        }
    }

    private ContactDto parsePhone(final CSVRecord record, final String fieldName) {
        String value = getOrNull(record, fieldName);
        if (value == null) {
            return null;
        }

        ContactDto contact = new ContactDto();
        contact.setType("phone");
        contact.setData(value.trim());
        return contact;
    }
}
