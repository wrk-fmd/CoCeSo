package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.PdfService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.utils.PdfDocument;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class PdfServiceImpl implements PdfService {

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private LogService logService;

  @Autowired
  private IncidentService incidentService;

  @Autowired
  private UnitService unitService;

  @Override
  public PdfDocument getDocument(Rectangle pageSize, boolean fullDate, HttpServletResponse response, Locale locale) throws IOException, DocumentException {
    PdfDocument export = new PdfDocument(pageSize, fullDate, this, locale);
    export.start(response);
    return export;
  }

  @Override
  public String getMessage(String code, Object[] args, Locale locale) {
    return messageSource.getMessage(code, args, locale);
  }

  @Override
  public String getMessage(String code, Object[] args, String text, Locale locale) {
    return messageSource.getMessage(code, args, text, locale);
  }

  @Override
  public List<Incident> getIncidents(Concern concern) {
    List<Incident> incidents = incidentService.getAll(concern);
    Collections.sort(incidents);
    return incidents;
  }

  @Override
  public List<Unit> getUnits(Concern concern) {
    List<Unit> units = unitService.getAll(concern);
    Collections.sort(units);
    return units;
  }

  @Override
  public List<LogEntry> getLogCustom(Concern concern) {
    return logService.getCustom(concern);
  }

  @Override
  public List<LogEntry> getLogByIncident(Incident incident) {
    return logService.getByIncident(incident);
  }

  @Override
  public List<LogEntry> getLogByUnit(Unit unit) {
    return logService.getByUnit(unit);
  }

  @Override
  public Incident getIncidentById(int incidentId) {
    return incidentService.getById(incidentId);
  }

  @Override
  public Unit getUnitById(int unitId) {
    return unitService.getById(unitId);
  }

  @Override
  public Map<Unit, TaskState> getRelatedUnits(Incident incident) {
    return unitService.getRelated(incident);
  }

  @Override
  public Timestamp getLastUpdate(Incident incident, Unit unit) {
    return logService.getLastTaskStateUpdate(incident, unit);
  }

}
