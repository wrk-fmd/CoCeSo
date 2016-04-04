package at.wrk.coceso.niu;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.importer.UserImporter;
import at.wrk.coceso.importer.ImportException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NiuUserImporter implements UserImporter {

  private static final String LASTNAME = "Nachname";
  private static final String FIRSTNAME = "Vorname";
  private static final String PERSONNELID = "DNr.";
  private static final String[] TELEPHONE_FIELDS = {
    "Handy privat", "Handy geschäftlich", "Handy WRK"
  };

  private static final char delimiter = '|';

  private static final Logger LOG = LoggerFactory.getLogger(NiuUserImporter.class);

  @Override
  public Collection<User> updateUsers(String data, Collection<User> existing) throws ImportException {
    // Put all existing users into a cache, indexed with personnel ID, lastname and firstname
    // Last occurence of combination will be kept
    Map<String, User> cache = existing.stream().collect(Collectors.toMap(
        u -> u.getPersonnelId() + "|" + u.getLastname() + "|" + u.getFirstname(), Function.identity()));

    Iterable<CSVRecord> records;
    try {
      records = CSVParser.parse(data, CSVFormat.RFC4180.withDelimiter(delimiter).withHeader());
    } catch (IOException e) {
      throw new ImportException("Could not parse users CSV file", e);
    }

    List<User> updated = new LinkedList<>();
    for (CSVRecord record : records) {
      // Parse personnel ID from CSV
      int personnelId;
      try {
        personnelId = Integer.parseInt(record.get(PERSONNELID));
      } catch (NumberFormatException e) {
        LOG.debug("Invalid personnel ID while parsing, defaulting to -1");
        personnelId = -1;
      }

      String lastname = record.get(LASTNAME), firstname = record.get(FIRSTNAME);

      // Read telephone numbers
      boolean notFirst = false;
      StringBuilder contact = new StringBuilder();
      for (String field : TELEPHONE_FIELDS) {
        String tel = record.get(field).trim();
        if (!tel.isEmpty()) {
          if (notFirst) {
            contact.append("\n");
          }
          contact.append(tel);
          notFirst = true;
        }
      }

      // Find existing user
      User u = cache.get(personnelId + "|" + lastname + "|" + firstname);
      if (u != null && (personnelId != u.getPersonnelId()
          || !lastname.equals(u.getLastname()) || !firstname.equals(u.getFirstname()))) {
        u = null;
      }

      if (u == null) {
        u = new User(personnelId, lastname, firstname);
      }
      u.setContact(contact.toString());

      updated.add(u);
    }

    return updated;
  }
}
