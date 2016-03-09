package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.utils.PdfDocument;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import java.io.IOException;
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

  PdfDocument getDocument(Rectangle pageSize, boolean fullDate, HttpServletResponse response, Locale locale) throws IOException, DocumentException;

  String getMessage(String code, Object[] args, Locale locale);

  String getMessage(String code, Object[] args, String text, Locale locale);

  List<Incident> getIncidents(Concern concern);

  List<Unit> getUnits(Concern concern);

  List<LogEntry> getLogCustom(Concern concern);

  List<LogEntry> getLogByIncident(Incident incident);

  List<LogEntry> getLogByUnit(Unit unit);

  Incident getIncidentById(int incidentId);

  Unit getUnitById(int unitId);

  Map<Unit, TaskState> getRelatedUnits(Incident incident);

  Timestamp getLastUpdate(Incident incident, Unit unit);

}
