// TODO This needs to be updated

//package at.wrk.coceso.importer;
//
//import at.wrk.coceso.entity.Concern;
//import at.wrk.coceso.entity.Unit;
//import at.wrk.coceso.entity.User;
//import at.wrk.coceso.entity.enums.Errors;
//import at.wrk.coceso.entity.enums.UnitType;
//import at.wrk.coceso.entity.helper.Changes;
//import at.wrk.coceso.entity.point.Point;
//import at.wrk.coceso.exceptions.ErrorsException;
//import at.wrk.coceso.service.UserService;
//import at.wrk.geocode.poi.PoiSupplier;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVRecord;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Component
//public class UnitImporter {
//  private static final Logger LOG = LoggerFactory.getLogger(UnitImporter.class);
//
//  private static final String CALL = "Rufname";
//  private static final String ANI = "ANI";
//  private static final String PORTABLE = "Mobil";
//  private static final String TYPE = "Typ";
//  private static final String INFO = "Info";
//  private static final String HOME = "Home";
//  private static final String COMMANDER = "Kdt";
//  private static final String CREW = "Crew";
//
//  private static final char DELIMITER = ',';
//
//  // TODO Using @Qualifier here feels kinda like hardcoding, maybe define that somewhere else
//  @Autowired
//  @Qualifier("ChainedPoi")
//  private PoiSupplier poiSupplier;
//
//  @Autowired
//  private UserService userService;
//
//  public Map<Unit, Changes> importUnits(String data, Concern concern, Collection<Unit> existing) {
//    // Put all existing units in cache
//    Map<String, Unit> cache = existing.stream().collect(Collectors.toMap(Unit::getCall, Function.identity()));
//
//    CSVParser records;
//    try {
//      records = CSVParser.parse(data, CSVFormat.RFC4180.withDelimiter(DELIMITER).withHeader());
//    } catch (IOException e) {
//      throw new ErrorsException(Errors.Import, e);
//    }
//
//    // Get crew fields
//    List<Integer> crewFields = new LinkedList<>();
//    Map<String, Integer> header = records.getHeaderMap();
//    if (header.containsKey(COMMANDER)) {
//      crewFields.add(header.get(COMMANDER));
//    }
//    crewFields.addAll(header.entrySet().stream().filter(e -> e.getKey().startsWith(CREW)).map(Map.Entry::getValue).collect(Collectors.toList()));
//
//    Map<Unit, Changes> updated = new HashMap<>();
//    for (CSVRecord record : records) {
//      if (!record.isSet(CALL)) {
//        continue;
//      }
//
//      Unit unit;
//      Changes changes = new Changes("unit");
//      boolean isNew;
//
//      String parsedUnitCall = record.get(CALL);
//      if (cache.containsKey(parsedUnitCall)) {
//        unit = cache.get(parsedUnitCall);
//        isNew = false;
//      } else {
//        unit = new Unit();
//        unit.setConcern(concern);
//        unit.setCall(parsedUnitCall);
//        changes.put("call", null, parsedUnitCall);
//        isNew = true;
//      }
//
//      if (record.isSet(ANI) && (isNew || StringUtils.isBlank(unit.getAni()))) {
//        String parsedAni = record.get(ANI);
//        unit.setAni(parsedAni);
//        changes.put("ani", null, parsedAni);
//      }
//
//      if (record.isSet(PORTABLE)) {
//        boolean portable = record.get(PORTABLE).equals("t");
//        if (portable != unit.isPortable()) {
//          unit.setPortable(portable);
//          changes.put("portable", unit.isPortable(), portable);
//        }
//      }
//
//      if (record.isSet(TYPE) && (isNew || unit.getType() == null)) {
//        String parsedUnitType = record.get(TYPE);
//        if (!StringUtils.isBlank(parsedUnitType)) {
//          try {
//            UnitType type = UnitType.valueOf(parsedUnitType.trim());
//            unit.setType(type);
//          } catch (EnumConstantNotPresentException e) {
//            LOG.debug("Could not parse unit type from '{}'.", parsedUnitType);
//          }
//        }
//      }
//
//      if (record.isSet(INFO) && (isNew || StringUtils.isBlank(unit.getInfo()))) {
//        String parsedInfo = record.get(INFO);
//        unit.setInfo(parsedInfo);
//        changes.put("info", null, parsedInfo);
//      }
//
//      if (record.isSet(HOME) && (isNew || Point.isEmpty(unit.getHome()))) {
//        String parsedHomePoint = record.get(HOME);
//        Point home = Point.create(parsedHomePoint, null, poiSupplier, null);
//        unit.setHome(home);
//        changes.put("home", null, home);
//      }
//
//      // Add crew
//      List<String> unmappedCrew = new LinkedList<>();
//      crewFields.forEach(i -> {
//        if (record.size() > i) {
//          String parsedPersonnelIdString = record.get(i).trim();
//          if (!parsedPersonnelIdString.isEmpty()) {
//            String[] parts = parsedPersonnelIdString.split(" ", 2);
//            // Try to parse personnel number
//            User user = null;
//            try {
//              int pnr = Integer.parseInt(parts[0]);
//              user = userService.getByPersonnelId(pnr);
//            } catch (NumberFormatException e) {
//              LOG.debug("Could not parse personnel ID from '{}'.", parsedPersonnelIdString);
//            }
//
//            if (user == null) {
//              unmappedCrew.add(parsedPersonnelIdString);
//            } else {
//              unit.addCrew(user);
//            }
//          }
//        }
//      });
//
//      // Prepend unknown users to info field
//      if (!unmappedCrew.isEmpty()) {
//        String info = unit.getInfo();
//        if (StringUtils.isBlank(info) || !info.startsWith("Crew:\n")) {
//          unit.setInfo("Crew:\n" + StringUtils.join(unmappedCrew, '\n') + (StringUtils.isBlank(info) ? "" : "\n\n" + info));
//        }
//      }
//
//      cache.put(unit.getCall(), unit);
//      updated.put(unit, changes);
//    }
//
//    return updated;
//  }
//}
