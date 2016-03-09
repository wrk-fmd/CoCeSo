package at.wrk.coceso.importer;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.Errors;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.exceptions.ErrorsException;
import at.wrk.coceso.service.PointService;
import at.wrk.coceso.service.UserService;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnitImporter {

  private static final String CALL = "Call";
  private static final String ANI = "ANI";
  private static final String PORTABLE = "Mobil";
  private static final String INFO = "Info";
  private static final String HOME = "Home";
  private static final String COMMANDER = "Kdt";
  private static final String CREW = "Crew";

  private static final char delimiter = ',';

  @Autowired
  private PointService pointService;

  @Autowired
  private UserService userService;

  public Map<Unit, Changes> importUnits(String data, Concern concern, Collection<Unit> existing) {
    // Put all existing units in cache
    Map<String, Unit> cache = existing.stream().collect(Collectors.toMap(Unit::getCall, Function.identity()));

    CSVParser records;
    try {
      records = CSVParser.parse(data, CSVFormat.RFC4180.withDelimiter(delimiter).withHeader());
    } catch (IOException e) {
      throw new ErrorsException(Errors.Import, e);
    }

    // Get crew fields
    List<Integer> crewFields = new LinkedList<>();
    Map<String, Integer> header = records.getHeaderMap();
    if (header.containsKey(COMMANDER)) {
      crewFields.add(header.get(COMMANDER));
    }
    crewFields.addAll(header.entrySet().stream().filter(e -> e.getKey().startsWith(CREW)).map(Map.Entry::getValue).collect(Collectors.toList()));

    Map<Unit, Changes> updated = new HashMap<>();
    for (CSVRecord record : records) {
      if (!record.isSet(CALL)) {
        continue;
      }

      Unit unit;
      Changes changes = new Changes("unit");
      boolean isNew;

      String csvval = record.get(CALL);
      if (cache.containsKey(csvval)) {
        unit = cache.get(csvval);
        isNew = false;
      } else {
        unit = new Unit();
        unit.setConcern(concern);
        unit.setCall(csvval);
        changes.put("call", null, csvval);
        isNew = true;
      }

      if (record.isSet(ANI) && (isNew || StringUtils.isBlank(unit.getAni()))) {
        csvval = record.get(ANI);
        unit.setAni(csvval);
        changes.put("ani", null, csvval);
      }

      if (record.isSet(INFO) && (isNew || StringUtils.isBlank(unit.getInfo()))) {
        csvval = record.get(INFO);
        unit.setInfo(csvval);
        changes.put("info", null, csvval);
      }

      if (record.isSet(HOME) && (isNew || Point.isEmpty(unit.getHome()))) {
        csvval = record.get(HOME);
        Point home = pointService.createIfNotExists(new Point(csvval));
        unit.setHome(home);
        changes.put("home", null, home);
      }

      // Add crew
      List<String> unmappedCrew = new LinkedList<>();
      crewFields.forEach(i -> {
        if (record.size() > i) {
          String val = record.get(i).trim();
          String[] parts = val.split(" ", 2);
          // Try to parse personnel number
          User user = null;
          try {
            int pnr = Integer.parseInt(parts[0]);
            user = userService.getByPersonnelId(pnr);
          } catch (NumberFormatException e) {
          }

          if (user == null) {
            unmappedCrew.add(val);
          } else {
            unit.addCrew(user);
          }
        }
      });

      // Prepend unknown users to info field
      if (!unmappedCrew.isEmpty()) {
        String info = unit.getInfo();
        if (StringUtils.isBlank(info) || !info.startsWith("Crew:\n")) {
          unit.setInfo("Crew:\n" + StringUtils.join(unmappedCrew, '\n') + (info == null ? "" : "\n\n" + info));
        }
      }

      cache.put(unit.getCall(), unit);
      updated.put(unit, changes);
    }

    return updated;
  }
}
