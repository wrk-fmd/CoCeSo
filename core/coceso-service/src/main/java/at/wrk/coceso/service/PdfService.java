package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public interface PdfService {

  void generateReport(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale);

  void generateDump(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale);

  void generateTransport(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale);

  void generatePatients(Concern concern, HttpServletResponse response, Locale locale);

  List<LogEntry> getLogByIncident(Incident incident);

  List<LogEntry> getLogByUnit(Unit unit);

  Map<Unit, TaskState> getRelatedUnits(Incident incident);

  Timestamp getLastUpdate(Incident incident, Unit unit);

}
