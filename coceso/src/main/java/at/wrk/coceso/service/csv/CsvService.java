package at.wrk.coceso.service.csv;

import at.wrk.coceso.entity.Person;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class CsvService {

    private static final
    String SURNAME = "Nachname";

    private static final
    String GIVENNAME = "Vorname";

    private static final
    String PERSONALID = "DNr.";

    private static final
    String[] TELEPHONE_FIELDS = {
             "Handy privat"
            ,"Handy geschäftlich"
            ,"Handy WRK"
    };

    private static final
    char delimiter = '|';


    private static final
    Logger LOG = Logger.getLogger(CsvService.class);

    public Set<Person> parsePersons(String csvBody) throws CsvParseException {
        Set<Person> persons = new HashSet<>();

        Iterable<CSVRecord> records;
        try {
            records = CSVParser.parse( csvBody, CSVFormat.RFC4180.withDelimiter(delimiter).withHeader() );
        } catch (IOException e) {
            throw new CsvParseException("Couldn't parse Person CSV", e);
        }

        for(CSVRecord record : records) {
            // parse Personal ID from CSV
            int dnr;
            try {
                dnr = Integer.parseInt(record.get(PERSONALID));
            } catch(NumberFormatException e) {
                LOG.debug("invalid personal ID (DNr) while parsing. set default to -1");
                dnr = -1;
            }

            // Read telephone numbers
            boolean notfirst = false;
            StringBuilder contact = new StringBuilder();
            for(String tel : TELEPHONE_FIELDS) {
                String currentTelNo = record.get(tel);
                if( !currentTelNo.isEmpty() ) {
                    if(notfirst)
                        contact.append("\n");
                    contact.append(currentTelNo);
                    notfirst = true;
                }
            }

            // Add person to return-set
            persons.add( new Person(record.get(GIVENNAME), record.get(SURNAME), dnr, contact.toString()) );
        }


        return persons;
    }
}
