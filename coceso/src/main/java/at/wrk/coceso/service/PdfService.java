package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.ChangePair;
import at.wrk.coceso.entity.helper.JsonContainer;
import at.wrk.coceso.utils.PdfStyle;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

@Service
public class PdfService {

  private final static Logger LOG = Logger.getLogger(PdfService.class);

  private final static String timeFormat = "HH:mm:ss";

  private final static String dateTimeFormat = "dd.MM.yy HH:mm:ss";

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private LogService logService;

  @Autowired
  private PatientService patientService;

  @Autowired
  private UnitService unitService;

  private Document document;

  private Locale locale;

  private boolean fullDate;

  // General methods
  public void start(Rectangle pageSize, boolean fullDate, HttpServletResponse response, Locale locale) throws IOException, DocumentException {
    if (document != null) {
      LOG.warn("Document was already opened!");
      return;
    }

    this.fullDate = fullDate;
    this.locale = locale;
    document = new Document(pageSize);

    PdfWriter.getInstance(document, response.getOutputStream());
    document.open();
  }

  public void addTitle(String title, Operator user) throws DocumentException {
    if (document == null) {
      LOG.warn("Document not initalized!");
      return;
    }

    document.addTitle(title);
    document.addAuthor("CoCeSo");
    document.addCreator("CoCeSo - " + user.getUsername());

    // TODO localization
    Paragraph p = new Paragraph();
    addEmptyLine(p, 1);

    Paragraph p0 = new Paragraph(title, PdfStyle.titleFont);
    p0.setAlignment(Element.ALIGN_CENTER);
    p.add(p0);
    addEmptyLine(p, 1);

    Paragraph p1 = new Paragraph("Bericht erstellt: " + user.getGiven_name() + " " + user.getSur_name()
            + ", " + new java.text.SimpleDateFormat(dateTimeFormat).format(new Date()), PdfStyle.subTitleFont);
    p1.setAlignment(Element.ALIGN_CENTER);
    p.add(p1);

    document.add(p);
  }

  public void addLastPage() throws DocumentException {
    if (document == null) {
      LOG.warn("Document not initalized!");
      return;
    }

    document.add(new Paragraph("Dokument vollständig erstellt: "
            + new java.text.SimpleDateFormat(dateTimeFormat).format(new Date())));
  }

  public void send() {
    if (document != null) {
      document.close();
      document = null;
    }
  }

  // Final report
  public void addConcernInfo(Concern concern) throws DocumentException {
    if (document == null) {
      LOG.warn("Document not initalized!");
      return;
    }

    Paragraph p = new Paragraph();
    addEmptyLine(p, 3);

    if (!concern.getInfo().trim().isEmpty()) {
      p.add(new Paragraph("Infos zur Ambulanz:\n" + concern.getInfo(), PdfStyle.defFont));
    }

    document.add(p);
    document.newPage();
  }

  public void addStatistics(List<Incident> incidents) throws DocumentException {
    if (document == null) {
      LOG.warn("Document not initalized!");
      return;
    }

    int task, taskBlue, transport, transportBlue, relocation, relocationBlue, other, otherBlue;
    task = taskBlue = transport = transportBlue = relocation = relocationBlue = other = otherBlue = 0;

    for (Incident incident : incidents) {
      switch (incident.getType()) {
        case Task:
          task++;
          if (incident.getBlue()) {
            taskBlue++;
          }
          break;
        case Transport:
          transport++;
          if (incident.getBlue()) {
            transportBlue++;
          }
          break;
        case Relocation:
          relocation++;
          if (incident.getBlue()) {
            relocationBlue++;
          }
          break;
        default:
          other++;
          if (incident.getBlue()) {
            otherBlue++;
          }
          break;
      }
    }

    PdfPTable table = new PdfPTable(new float[]{3, 1, 1});
    table.setWidthPercentage(100);

    table.addCell("");
    table.addCell(messageSource.getMessage("label.report.total", null, locale));
    table.addCell(messageSource.getMessage("label.report.stat_blue", null, locale));

    table.addCell(messageSource.getMessage("label.incident.type.task", null, locale) + " / " + messageSource.getMessage("label.incident.type.task.blue", null, locale));
    table.addCell("" + task);
    table.addCell("" + taskBlue);

    table.addCell(messageSource.getMessage("label.incident.type.transport", null, locale));
    table.addCell("" + transport);
    table.addCell("" + transportBlue);

    table.addCell(messageSource.getMessage("label.incident.type.relocation", null, locale));
    table.addCell("" + relocation);
    table.addCell("" + relocationBlue);

    table.addCell(messageSource.getMessage("label.report.incident.other", null, locale));
    table.addCell("" + other);
    table.addCell("" + otherBlue);

    document.add(new Paragraph(messageSource.getMessage("label.report.statistics", null, locale), PdfStyle.titleFont));
    document.add(new Paragraph(" "));
    document.add(table);

    document.add(new Paragraph(" "));
    document.newPage();
  }

  public void addCustomLog(Concern concern) throws DocumentException {
    if (document == null) {
      LOG.warn("Document not initalized!");
      return;
    }

    List<LogEntry> logs = logService.getCustom(concern.getId());
    Collections.reverse(logs);

    Paragraph p = new Paragraph();
    Paragraph h = new Paragraph(messageSource.getMessage("label.log.custom", null, locale), PdfStyle.title2Font);
    p.add(h);

    PdfPTable table = new PdfPTable(new float[]{1, 1, 3, 1});
    table.setWidthPercentage(100);

    addCell(table, messageSource.getMessage("label.log.timestamp", null, locale));
    addCell(table, messageSource.getMessage("label.operator", null, locale));
    addCell(table, messageSource.getMessage("label.log.text", null, locale));
    addCell(table, messageSource.getMessage("label.unit", null, locale));

    for (LogEntry log : logs) {
      addCell(table, getFormattedTime(log.getTimestamp()));
      addCell(table, log.getUser().getUsername());
      addCell(table, log.getText());
      addCell(table, getUnitTitle(log.getUnit()));
    }

    p.add(table);
    document.add(p);
    document.newPage();
  }

  public void addUnits(int concern_id) throws DocumentException {
    if (document == null) {
      LOG.warn("Document not initalized!");
      return;
    }

    List<Unit> units = unitService.getAll(concern_id);
    Collections.sort(units, new Comparator<Unit>() {
      @Override
      public int compare(Unit o1, Unit o2) {
        if (o1 == null || o1.getCall() == null) {
          return 1;
        }
        return o1.getCall().compareTo(o2.getCall());
      }
    });

    document.add(new Paragraph(messageSource.getMessage("label.units", null, locale), PdfStyle.titleFont));
    document.add(new Paragraph(" "));

    for (Unit unit : units) {
      List<LogEntry> logs = logService.getByUnitId(unit.getId());
      Collections.reverse(logs);

      Paragraph p = new Paragraph();
      Paragraph h = new Paragraph(unit.getCall() + " - #" + unit.getId(), PdfStyle.title2Font);
      Paragraph s = new Paragraph((unit.getAni() == null || unit.getAni().isEmpty() ? "" : ("ANI: " + unit.getAni()) + "\n")
              + messageSource.getMessage("label.unit.home", null, locale) + ": " + (unit.getHome() == null ? "N/A" : unit.getHome()));

      p.add(h);
      p.add(s);

      PdfPTable table = new PdfPTable(new float[]{2, 2, 4, 3, 1, 5});
      table.setWidthPercentage(100);

//      addCell(table, messageSource.getMessage("label.log.timestamp", null, locale));
//      addCell(table, messageSource.getMessage("label.operator", null, locale));
//      addCell(table, messageSource.getMessage("label.log.text", null, locale));
//      addCell(table, messageSource.getMessage("label.incident", null, locale));
//      addCell(table, messageSource.getMessage("label.task.state", null, locale));
//      addCell(table, messageSource.getMessage("label.log.changes", null, locale));
      for (LogEntry log : logs) {
        addCell(table, getFormattedTime(log.getTimestamp()));
        addCell(table, log.getUser().getUsername());
        addCell(table, getLogText(log));
        addCell(table, getIncidentTitle(log.getIncident()));
        addCell(table, getTaskState(log.getState()));
        addCell(table, getLogChanges(log.getChanges()));
      }

      p.add(table);
      addEmptyLine(p, 1);
      document.add(p);
    }
    document.newPage();
  }

  // TODO History of Patientdata changes
  public void addIncidents(List<Incident> incidents) throws DocumentException {
    if (document == null) {
      LOG.warn("Document not initalized!");
      return;
    }

    document.add(new Paragraph(messageSource.getMessage("label.incidents", null, locale), PdfStyle.titleFont));
    document.add(new Paragraph(" "));

    for (Incident incident : incidents) {
      // Single Unit Incidents are fully logged by UnitStats
      if (incident.getType().isSingleUnit()) {
        continue;
      }

      List<LogEntry> logs = logService.getByIncidentId(incident.getId());
      Collections.reverse(logs);

      Paragraph p = new Paragraph();

      Paragraph h = new Paragraph(getIncidentTitle(incident), PdfStyle.title2Font);

      Paragraph s = new Paragraph("BO: " + (incident.getBo() == null ? "N/A" : incident.getBo()) + "\n"
              + "AO: " + (incident.getAo() == null ? "N/A" : incident.getAo()), PdfStyle.descrFont);
      p.add(h);
      p.add(s);

      Patient patient = patientService.getById(incident.getId());
      if (patient != null) {
        Paragraph pat = new Paragraph(messageSource.getMessage("label.patient", null, locale) + ": "
                + patient.getGiven_name() + "" + patient.getSur_name());
        /*messageSource.getMessage("label.patient.sex", null, locale) + ": " + patient.getSex() + "\n" +
         messageSource.getMessage("label.patient.insurance_number", null, locale) + ": " + patient.getSur_name() + "\n" +
         messageSource.getMessage("label.patient.externalID", null, locale) + ": " + patient.getExternalID() + "\n" +
         messageSource.getMessage("label.patient.diagnosis", null, locale) + ": " + patient.getDiagnosis() + "\n" +
         messageSource.getMessage("label.patient.erType", null, locale) + ": " + patient.getErType() + "\n" +
         messageSource.getMessage("label.patient.info", null, locale) + ": " + patient.getInfo() + "\n" );*/

        p.add(pat);
      }

      PdfPTable table = new PdfPTable(new float[]{2, 2, 4, 2, 1, 5});
      table.setWidthPercentage(100);

//      addCell(table, messageSource.getMessage("label.log.timestamp", null, locale));
//      addCell(table, messageSource.getMessage("label.operator", null, locale));
//      addCell(table, messageSource.getMessage("label.log.text", null, locale));
//      addCell(table, messageSource.getMessage("label.unit", null, locale));
//      addCell(table, messageSource.getMessage("label.task.state", null, locale));
//      addCell(table, messageSource.getMessage("label.log.changes", null, locale));
      for (LogEntry log : logs) {
        addCell(table, getFormattedTime(log.getTimestamp()));
        addCell(table, log.getUser().getUsername());
        addCell(table, getLogText(log));
        addCell(table, getUnitTitle(log.getUnit()));
        addCell(table, getTaskState(log.getState()));
        addCell(table, getLogChanges(log.getChanges()));
      }

      p.add(table);
      addEmptyLine(p, 1);
      document.add(p);
    }
    document.newPage();
  }

  private static void addEmptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(" "));
    }
  }

  private static void addCell(PdfPTable table, String content) {
    addCell(table, content, PdfPCell.TOP + PdfPCell.BOTTOM);
  }

  private static void addCell(PdfPTable table, String content, int border) {
    PdfPCell cell = new PdfPCell(new Phrase(content));
    cell.setBorder(border);
    table.addCell(cell);
  }

  private static void addCell(PdfPTable table, PdfPTable content) {
    table.addCell(content);
//    PdfPCell cell = new PdfPCell(content);
//    cell.setBorder(PdfPCell.TOP + PdfPCell.BOTTOM);
//    table.addCell(cell);
  }

  private String getFormattedTime(Timestamp timestamp) {
    return new java.text.SimpleDateFormat(fullDate ? dateTimeFormat : timeFormat).format(timestamp);
  }

  private String getLogText(LogEntry log) {
    return log.getType() == LogEntryType.CUSTOM
            ? log.getText()
            : messageSource.getMessage("descr." + log.getType(), null, log.getText(), locale);
  }

  private String getIncidentTitle(Incident inc) {
    if (inc == null) {
      return "";
    }

    String title = "#" + inc.getId() + " - ";

    if (inc.getType() == IncidentType.Task) {
      if (inc.getBlue() == null || !inc.getBlue()) {
        title += messageSource.getMessage("label.incident.type.task", null, locale);
      } else {
        title += messageSource.getMessage("label.incident.type.task.blue", null, locale);
      }
    } else {
      title += messageSource.getMessage("label.incident.type." + inc.getType().toString().toLowerCase(), null, inc.getType().toString(), locale);
    }

    return title;
  }

  private String getUnitTitle(Unit unit) {
    return unit != null ? "#" + unit.getId() + " " + unit.getCall() : "";
  }

  private String getTaskState(TaskState state) {
    return state != null ? messageSource.getMessage("label.task.state." + state.toString().toLowerCase(), null, state.toString(), locale) : "";
  }

  private PdfPTable getLogChanges(JsonContainer changes) {
    if (changes == null || changes.getData() == null) {
      return null;
    }

    PdfPTable table = new PdfPTable(new float[]{1, 1, 1});
    table.setWidthPercentage(100);

    for (Map.Entry<String, ChangePair<Object>> entry : changes.getData().entrySet()) {
      ChangePair value = entry.getValue();
      addCell(table, messageSource.getMessage("label." + changes.getType() + "." + entry.getKey(), null, entry.getKey(), locale) + ": ", PdfPCell.NO_BORDER);
      addCell(table, value.getOldValue() != null ? value.getOldValue() + "" : "", PdfPCell.NO_BORDER);
      addCell(table, value.getNewValue() != null ? value.getNewValue() + "" : "[empty]", PdfPCell.NO_BORDER);
    }
    return table;
  }

}
