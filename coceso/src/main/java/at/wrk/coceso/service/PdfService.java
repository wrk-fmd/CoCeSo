package at.wrk.coceso.service;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.utils.PdfDocument;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

@Service
public class PdfService {

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private LogService logService;

  @Autowired
  private IncidentService incidentService;

  @Autowired
  private PatientService patientService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private UnitService unitService;


  public PdfDocument getDocument(Rectangle pageSize, boolean fullDate, HttpServletResponse response, Locale locale) throws IOException, DocumentException {
    PdfDocument export = new PdfDocument(pageSize, fullDate, this, locale);
    export.start(response);
    return export;
  }

  public String getMessage(String code, Object[] args, Locale locale) {
    return messageSource.getMessage(code, args, locale);
  }

  public String getMessage(String code, Object[] args, String text, Locale locale) {
    return messageSource.getMessage(code, args, text, locale);
  }

  public List<Incident> getIncidents(int concern_id) {
    List<Incident> incidents = incidentService.getAll(concern_id);
    Collections.sort(incidents, new Comparator<Incident>() {
      @Override
      public int compare(Incident a, Incident b) {
        if (a == null || b == null) {
          return a == null ? (b == null ? 0 : 1) : -1;
        }
        return a.getId() - b.getId();
      }
    });
    return incidents;
  }

  public List<Unit> getUnits(int concern_id) {
    List<Unit> units = unitService.getAll(concern_id);
    Collections.sort(units, new Comparator<Unit>() {
      @Override
      public int compare(Unit a, Unit b) {
        if (a == null || a.getCall() == null) {
          return 1;
        }
        return a.getCall().compareTo(b.getCall());
      }
    });

    return units;
  }

  public List<LogEntry> getLogCustom(int concern_id) {
    return logService.getCustom(concern_id);
  }

  public List<LogEntry> getLogByIncidentId(int id) {
    return logService.getByIncidentId(id);
  }

  public List<LogEntry> getLogByUnitId(int id) {
    return logService.getByUnitId(id);
  }

  public Incident getIncidentById(int id) {
    return incidentService.getById(id);
  }

  public Patient getPatientById(int id) {
    return patientService.getById(id);
  }

  public Unit getUnitById(int id) {
    return unitService.getById(id);
  }

  public Map<Unit, TaskState> getRelatedUnits(int id) {
    return unitService.getRelated(id);
  }

  public Timestamp getLastUpdate(int incident_id, int unit_id) {
    return taskService.getLastUpdate(incident_id, unit_id);
  }

}
