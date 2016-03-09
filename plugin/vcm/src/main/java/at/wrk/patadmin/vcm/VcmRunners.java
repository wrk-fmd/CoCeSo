package at.wrk.patadmin.vcm;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Medinfo;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VcmRunners implements Iterable<Medinfo> {

  private final static Logger LOG = LoggerFactory.getLogger(VcmRunners.class);

  private static final Set<String> ignore = new HashSet(Arrays.asList(
      "%", "0", ":-)", "???",
      "ansprechperson handynummer", "any", "aucun", "aucune", "aucunes",
      "derzeit keine", "dna", "getestet - keine", "good", "i don\'t have any",
      "k", "k.b", "kb", "kei ne", "keie", "keien", "keijne", "kein",
      "keine (nicht bekannt)", "keine bekannat", "keine bekannt", "keine bekannten", "keine medikamente",
      "keine regelmäßig", "keine teilnahme", "keine", "keines", "kene", "kine", "leine", "mein", "mir nicht bekannt",
      "n", "n.a", "n.b", "n.z", "n/a", "n/k", "n0", "na", "ne", "neg", "nei", "neim", "nein", "neine", "nej", "nen",
      "nessuna", "nessuno", "neun", "never", "ni", "nicht bekannt", "nicht", "nichts bekannt", "nichts",
      "nie", "nien", "nil", "nin", "nin", "nine", "nn", "nno", "no, forget it", "no any", "no problems", "no", "noe",
      "non", "none at present", "none known", "none", "nonw", "not applicable", "not known", "not recognized", "not",
      "nothing", "nove", "np", "null", "néant", "nö", "sans objet", "x"
  ));

  private static final String trim = " -./,!";

  private static final Map<String, Character> events = new HashMap<>();

  static {
    events.put("1", 'M');
    events.put("2", 'H');
    events.put("3", 'S');
    events.put("4", 'J');
    events.put("5", 'K');
  }

  private final Iterator<CSVRecord> records;
  private final Concern concern;

  public VcmRunners(File csv, Concern concern) throws IOException {
    LOG.info(String.format("Start reading CSV runner data"));
    this.concern = concern;
    CSVParser data = CSVParser.parse(csv, Charset.forName("UTF-8"), CSVFormat.MYSQL.withHeader().withNullString("NULL"));
    if (!data.getHeaderMap().keySet().containsAll(Arrays.asList("STARTNR", "EVENT", "NAME"))) {
      throw new IOException("Unknown format");
    }
    this.records = data.iterator();
    LOG.info(String.format("Loaded CSV runner data"));
  }

  @Override
  public Iterator<Medinfo> iterator() {
    return new Iterator<Medinfo>() {

      @Override
      public boolean hasNext() {
        return records.hasNext();
      }

      @Override
      public Medinfo next() {
        CSVRecord record = records.next();
        Medinfo runner = new Medinfo();

        Map<String, String> row = record.toMap();
        Map<String, Object> data = new HashMap();

        runner.setConcern(concern);

        for (Map.Entry<String, String> field : row.entrySet()) {
          String key = field.getKey(), value = field.getValue();

          if (value == null) {
            continue;
          }

          value = StringUtils.strip(value, trim);
          if (ignore.contains(value.toLowerCase())) {
            value = "";
          }

          switch (key) {
            case "NAME":
              if (StringUtils.isNotBlank(value)) {
                int pos = value.lastIndexOf(' ');
                if (pos < 0) {
                  runner.setLastname(value);
                } else {
                  runner.setFirstname(value.substring(0, pos));
                  runner.setLastname(value.substring(pos + 1));
                }
              }
              break;
            case "STARTNR":
              if (StringUtils.isNotBlank(value)) {
                String event = row.get("EVENT");
                if (events.containsKey(event)) {
                  runner.setExternalId(events.get(event) + "-" + value);
                } else {
                  runner.setExternalId(value);
                }
                if (runner.getExternalId().length() > 20) {
                  LOG.info(runner.getExternalId());
                }
              }
              break;
            case "YEAR":
              if (StringUtils.isNotBlank(value)) {
                try {
                  runner.setBirthday(LocalDate.of(Integer.parseInt(value), 1, 1));
                } catch (NumberFormatException | DateTimeException e) {
                }
              }
              break;
            case "EVENT":
            case "QResult":
              try {
                data.put(key, Integer.parseInt(value));
              } catch (NumberFormatException e) {
              }
              break;
            case "QHeart":
            case "QChest":
            case "QBreath":
            case "QPassOut":
            case "QBones":
            case "QReason":
              data.put(key, value.equals("y"));
              break;
            case "MAILINFORMATION":
            case "ANONYMIZED":
            case "LABOR":
            case "ECG":
            case "SUSPENSION":
            case "SUSPENSION_QUESTION":
              data.put(key, value.equals("1"));
              break;
            default:
              data.put(key, value);
              break;
          }
        }

        if (!data.isEmpty()) {
          runner.setData(data);
        }

        return runner;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

}
