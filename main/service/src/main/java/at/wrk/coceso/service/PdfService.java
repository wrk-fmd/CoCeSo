package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.TaskState;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface PdfService {

  void generateReport(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale, User user);

  void generateDump(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale, User user);

  void generateTransport(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale, User user);

  void generatePatients(Concern concern, HttpServletResponse response, Locale locale, User user);

  List<LogEntry> getLogByIncident(Incident incident);

  List<LogEntry> getLogByUnit(Unit unit);

  Map<Unit, TaskState> getRelatedUnits(Incident incident);

  Timestamp getLastUpdate(Incident incident, Unit unit);

}
