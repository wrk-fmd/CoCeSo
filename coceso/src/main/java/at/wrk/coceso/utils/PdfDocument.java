package at.wrk.coceso.utils;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.ChangePair;
import at.wrk.coceso.entity.helper.JsonContainer;
import at.wrk.coceso.service.PdfService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class PdfDocument extends Document {

  private final static String timeFormat = "HH:mm:ss";

  private final static String dateTimeFormat = "dd.MM.yy HH:mm:ss";

  private final PdfService pdfService;

  private final Locale locale;

  private final boolean fullDate;

  public PdfDocument(Rectangle pageSize, boolean fullDate, PdfService pdfService, Locale locale) {
    super(pageSize);
    this.fullDate = fullDate;
    this.pdfService = pdfService;
    this.locale = locale;
  }

  public void start(HttpServletResponse response) throws DocumentException, IOException {
    PdfWriter.getInstance(this, response.getOutputStream());
    this.open();
  }

  public void addFrontPage(String title, Concern concern, Operator user) throws DocumentException {
    title = pdfService.getMessage(title, new String[]{concern.getName()}, title, locale);

    this.addTitle(title);
    this.addAuthor("CoCeSo");
    this.addCreator("CoCeSo - " + user.getUsername());

    Paragraph p = new Paragraph();
    addEmptyLine(p, 1);

    Paragraph p0 = new Paragraph(title, PdfStyle.titleFont);
    p0.setAlignment(Element.ALIGN_CENTER);
    p.add(p0);
    addEmptyLine(p, 1);

    Paragraph p1 = new Paragraph(pdfService.getMessage("label.pdf.created",
            new String[]{user.getGiven_name(), user.getSur_name(), new java.text.SimpleDateFormat(dateTimeFormat).format(new Date())},
            locale), PdfStyle.subTitleFont);
    p1.setAlignment(Element.ALIGN_CENTER);
    p.add(p1);

    if (!concern.getInfo().trim().isEmpty()) {
      addEmptyLine(p, 3);
      p.add(new Paragraph(pdfService.getMessage("label.pdf.infos", new String[]{concern.getInfo()}, locale)));
    }

    this.add(p);
    this.newPage();
  }

  public void addLastPage() throws DocumentException {
    this.add(new Paragraph(pdfService.getMessage("label.pdf.complete",
            new String[]{new java.text.SimpleDateFormat(dateTimeFormat).format(new Date())}, locale)));
  }

  public void addStatistics(List<Incident> incidents) throws DocumentException {
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

    PdfPTable table = new PdfPTable(new float[]{2, 1, 1});
    table.setWidthPercentage(50);
    table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
    table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

    table.addCell("");
    table.addCell(pdfService.getMessage("label.report.total", null, locale));
    table.addCell(pdfService.getMessage("label.report.stat_blue", null, locale));

    table.addCell(pdfService.getMessage("label.incident.type.task", null, locale) + " / " + pdfService.getMessage("label.incident.type.task.blue", null, locale));
    table.addCell("" + task);
    table.addCell("" + taskBlue);

    table.addCell(pdfService.getMessage("label.incident.type.transport", null, locale));
    table.addCell("" + transport);
    table.addCell("" + transportBlue);

    table.addCell(pdfService.getMessage("label.incident.type.relocation", null, locale));
    table.addCell("" + relocation);
    table.addCell("" + relocationBlue);

    table.addCell(pdfService.getMessage("label.report.incident.other", null, locale));
    table.addCell("" + other);
    table.addCell("" + otherBlue);

    this.add(new Paragraph(pdfService.getMessage("label.report.statistics", null, locale), PdfStyle.titleFont));
    this.add(new Paragraph(""));
    this.add(table);

    this.add(new Paragraph(""));
    this.newPage();
  }

  public void addCustomLog(int concern_id) throws DocumentException {
    List<LogEntry> logs = pdfService.getLogCustom(concern_id);
    Collections.reverse(logs);

    Paragraph p = new Paragraph();
    Paragraph h = new Paragraph(pdfService.getMessage("label.log.custom", null, locale), PdfStyle.title2Font);
    p.add(h);

    PdfPTable table = new PdfPTable(new float[]{1, 1, 3, 1});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

    table.addCell(pdfService.getMessage("label.log.timestamp", null, locale));
    table.addCell(pdfService.getMessage("label.operator", null, locale));
    table.addCell(pdfService.getMessage("label.log.text", null, locale));
    table.addCell(pdfService.getMessage("label.unit", null, locale));

    for (LogEntry log : logs) {
      table.addCell(getFormattedTime(log.getTimestamp()));
      table.addCell(log.getUser().getUsername());
      table.addCell(log.getText());
      table.addCell(getUnitTitle(log.getUnit()));
    }

    p.add(table);
    this.add(p);
    this.newPage();
  }

  /**
   * Add log entries and details for all non-single incidents (for final report)
   *
   * @param incidents
   * @throws DocumentException
   */
  public void addIncidentsLog(List<Incident> incidents) throws DocumentException {
    this.add(new Paragraph(pdfService.getMessage("label.incidents", null, locale), PdfStyle.titleFont));
    this.add(new Paragraph(" "));

    for (Incident incident : incidents) {
      if (incident.getType().isSingleUnit()) {
        continue;
      }

      Paragraph p = printIncidentTitle(incident);
      p.add(printIncidentDetails(incident));
      addEmptyLine(p, 1);
      p.add(printPatientDetails(incident.getId()));
      addEmptyLine(p, 1);
      p.add(printRelatedUnits(incident));
      addEmptyLine(p, 1);
      p.add(printIncidentLog(incident));
      addEmptyLine(p, 1);
      this.add(p);
    }
    this.newPage();
  }

  /**
   * Add all transports (for transport list)
   *
   * @param concern_id
   * @throws DocumentException
   */
  public void addTransports(int concern_id) throws DocumentException {
    this.add(new Paragraph(pdfService.getMessage("label.transportlist", null, locale), PdfStyle.titleFont));
    this.add(new Paragraph(" "));

    for (Incident incident : pdfService.getIncidents(concern_id)) {
      if (incident.getType() != IncidentType.Transport) {
        continue;
      }

      Paragraph p = printIncidentTitle(incident);
      p.add(printIncidentDetails(incident));
      addEmptyLine(p, 1);
      p.add(printPatientDetails(incident.getId()));
      addEmptyLine(p, 1);
      p.add(printRelatedUnits(incident));
      addEmptyLine(p, 1);
      this.add(p);
    }
    this.newPage();
  }

  /**
   * Get current state of not-done incidents (for dump)
   *
   * @param concern_id
   * @throws DocumentException
   */
  public void addIncidentsCurrent(int concern_id) throws DocumentException {
    this.add(new Paragraph(pdfService.getMessage("label.incidents", null, locale), PdfStyle.titleFont));
    this.add(new Paragraph(" "));

    for (Incident incident : pdfService.getIncidents(concern_id)) {
      if (incident.getType().isSingleUnit() || incident.getState() == IncidentState.Done) {
        continue;
      }

      Paragraph p = printIncidentTitle(incident);
      p.add(printIncidentDetails(incident));
      addEmptyLine(p, 1);
      p.add(printPatientDetails(incident.getId()));
      addEmptyLine(p, 1);
      p.add(printIncidentUnits(incident));
      addEmptyLine(p, 1);
      this.add(p);
    }
    this.newPage();
  }

  /**
   * Add log entries for all units (final report)
   *
   * @param concern_id
   * @throws DocumentException
   */
  public void addUnitsLog(int concern_id) throws DocumentException {
    this.add(new Paragraph(pdfService.getMessage("label.units", null, locale), PdfStyle.titleFont));
    this.add(new Paragraph(" "));

    for (Unit unit : pdfService.getUnits(concern_id)) {
      Paragraph p = printUnitTitle(unit);
      p.add(printUnitLog(unit));
      addEmptyLine(p, 1);
      this.add(p);
    }
    this.newPage();
  }

  /**
   * Add unit details and assigned incidents (current state for dump)
   *
   * @param concern_id
   * @throws DocumentException
   */
  public void addUnitsCurrent(int concern_id) throws DocumentException {
    this.add(new Paragraph(pdfService.getMessage("label.units", null, locale), PdfStyle.titleFont));
    this.add(new Paragraph(" "));

    for (Unit unit : pdfService.getUnits(concern_id)) {
      Paragraph p = printUnitTitle(unit);
      p.add(printUnitDetails(unit));
      addEmptyLine(p, 1);
      p.add(printUnitIncidents(unit));
      addEmptyLine(p, 1);
      this.add(p);
    }
    this.newPage();
  }

  private Paragraph printIncidentTitle(Incident inc) {
    Paragraph p = new Paragraph();
    p.add(new Paragraph(getIncidentTitle(inc), PdfStyle.title2Font));
    return p;
  }

  private Element printIncidentDetails(Incident inc) {
    PdfPTable table = new PdfPTable(new float[]{2, 4, 1, 2, 4, 0});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    table.addCell(pdfService.getMessage("label.incident.blue", null, locale) + ":");
    table.addCell(pdfService.getMessage(inc.getBlue() ? "label.yes" : "label.no", null, locale));
    table.addCell("");

    table.addCell(pdfService.getMessage("label.incident.state", null, locale) + ":");
    table.addCell(pdfService.getMessage("label.incident.state." + inc.getState().toString().toLowerCase(), null, inc.getState().toString(), locale));
    table.addCell("");

    if (inc.getType() == IncidentType.Task || inc.getType() == IncidentType.Transport) {
      table.addCell(pdfService.getMessage("label.incident.bo", null, locale) + ":");
      table.addCell(Point.isEmpty(inc.getBo())
              ? pdfService.getMessage("label.incident.nobo", null, locale)
              : inc.getBo().getInfo());
      table.addCell("");
    }

    table.addCell(pdfService.getMessage("label.incident.ao", null, locale) + ":");
    table.addCell(Point.isEmpty(inc.getAo())
            ? pdfService.getMessage("label.incident.noao", null, locale)
            : inc.getAo().getInfo());
    table.addCell("");

    if (inc.getInfo() != null && !inc.getInfo().isEmpty()) {
      table.addCell(pdfService.getMessage("label.incident.info", null, locale) + ":");
      table.addCell(inc.getInfo());
    }
    table.completeRow();

    if (inc.getCaller() != null && !inc.getCaller().isEmpty()) {
      table.addCell(pdfService.getMessage("label.incident.caller", null, locale) + ":");
      table.addCell(inc.getCaller());
      table.addCell("");
    }

    if (inc.getCasusNr() != null && !inc.getCasusNr().isEmpty()) {
      table.addCell(pdfService.getMessage("label.incident.casus", null, locale) + ":");
      table.addCell(inc.getCasusNr());
      table.addCell("");
    }
    table.completeRow();

    return table;
  }

  private Element printPatientDetails(int id) {
    Patient patient = pdfService.getPatientById(id);
    if (patient == null) {
      return null;
    }

    PdfPTable table = new PdfPTable(new float[]{2, 4, 1, 2, 4, 0});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    table.addCell(pdfService.getMessage("label.patient", null, locale) + ":");
    table.addCell(patient.getGiven_name() + " " + patient.getSur_name());
    table.addCell("");

    table.addCell(pdfService.getMessage("label.patient.sex", null, locale) + ":");
    table.addCell(pdfService.getMessage("label.patient.sex." + patient.getSex(), null, patient.getSex().toString(), locale));
    table.addCell("");

    if (patient.getInsurance_number() != null && !patient.getInsurance_number().isEmpty()) {
      table.addCell(pdfService.getMessage("label.patient.insurance_number", null, locale) + ":");
      table.addCell(patient.getInsurance_number());
      table.addCell("");
    }

    if (patient.getExternalID() != null && !patient.getExternalID().isEmpty()) {
      table.addCell(pdfService.getMessage("label.patient.externalID", null, locale) + ":");
      table.addCell(patient.getExternalID());
      table.addCell("");
    }
    table.completeRow();

    if (patient.getDiagnosis() != null && !patient.getDiagnosis().isEmpty()) {
      table.addCell(pdfService.getMessage("label.patient.diagnosis", null, locale) + ":");
      table.addCell(patient.getDiagnosis());
      table.addCell("");
    }

    if (patient.getErType() != null && !patient.getErType().isEmpty()) {
      table.addCell(pdfService.getMessage("label.patient.erType", null, locale) + ":");
      table.addCell(patient.getErType());
      table.addCell("");
    }
    table.completeRow();

    if (patient.getInfo() != null && !patient.getInfo().isEmpty()) {
      table.addCell(pdfService.getMessage("label.patient.info", null, locale) + ":");
      table.addCell(patient.getInfo());
    }
    table.completeRow();

    return table;
  }

  private Element printIncidentLog(Incident inc) {
    List<LogEntry> logs = pdfService.getLogByIncidentId(inc.getId());
    Collections.reverse(logs);

    Paragraph p = new Paragraph(pdfService.getMessage("label.log", null, locale), PdfStyle.descrFont);

    PdfPTable table = new PdfPTable(new float[]{2, 2, 4, 2, 1, 5});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

    table.addCell(pdfService.getMessage("label.log.timestamp", null, locale));
    table.addCell(pdfService.getMessage("label.operator", null, locale));
    table.addCell(pdfService.getMessage("label.log.text", null, locale));
    table.addCell(pdfService.getMessage("label.unit", null, locale));
    table.addCell(pdfService.getMessage("label.task.state", null, locale));
    table.addCell(pdfService.getMessage("label.log.changes", null, locale));
    for (LogEntry log : logs) {
      table.addCell(getFormattedTime(log.getTimestamp()));
      table.addCell(log.getUser().getUsername());
      table.addCell(getLogText(log));
      table.addCell(getUnitTitle(log.getUnit()));
      table.addCell(getTaskState(log.getState()));
      table.addCell(getLogChanges(log.getChanges()));
    }

    p.add(table);
    return p;
  }

  private Element printIncidentUnits(Incident inc) {
    if (inc.getUnits() == null || inc.getUnits().isEmpty()) {
      return null;
    }

    Paragraph p = new Paragraph(pdfService.getMessage("label.units", null, locale), PdfStyle.descrFont);

    PdfPTable table = new PdfPTable(new float[]{2, 1, 2});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

    table.addCell(pdfService.getMessage("label.unit", null, locale));
    table.addCell(pdfService.getMessage("label.task.state", null, locale));
    table.addCell(pdfService.getMessage("label.last_update", null, locale));
    for (Map.Entry<Integer, TaskState> entry : inc.getUnits().entrySet()) {
      Unit unit = pdfService.getUnitById(entry.getKey());

      table.addCell(unit != null ? getUnitTitle(unit) : "#" + entry.getKey());
      table.addCell(getTaskState(entry.getValue()));
      table.addCell(getFormattedTime(pdfService.getLastUpdate(inc.getId(), entry.getKey())));
    }

    p.add(table);
    return p;
  }

  private Element printRelatedUnits(Incident inc) {
    Map<Unit, TaskState> units = pdfService.getRelatedUnits(inc.getId());
    if (units == null || units.isEmpty()) {
      return null;
    }

    Paragraph p = new Paragraph(pdfService.getMessage("label.units", null, locale), PdfStyle.descrFont);

    PdfPTable table = new PdfPTable(new float[]{2, 1, 2});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

    table.addCell(pdfService.getMessage("label.unit", null, locale));
    table.addCell(pdfService.getMessage("label.task.state", null, locale));
    table.addCell(pdfService.getMessage("label.last_update", null, locale));
    for (Map.Entry<Unit, TaskState> entry : units.entrySet()) {
      Unit unit = entry.getKey();

      table.addCell(getUnitTitle(unit));
      table.addCell(getTaskState(entry.getValue()));
      table.addCell(getFormattedTime(pdfService.getLastUpdate(inc.getId(), unit.getId())));
    }

    p.add(table);
    return p;
  }

  private Paragraph printUnitTitle(Unit unit) {
    Paragraph p = new Paragraph();
    p.add(new Paragraph(unit.getCall() + " - #" + unit.getId(), PdfStyle.title2Font));
    return p;
  }

  private Element printUnitDetails(Unit unit) {
    PdfPTable table = new PdfPTable(new float[]{2, 4, 1, 2, 4, 0});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    table.addCell(pdfService.getMessage("label.unit.state", null, locale) + ":");
    table.addCell(pdfService.getMessage("label.unit.state." + unit.getState().toString().toLowerCase(), null, unit.getState().toString(), locale));
    table.addCell("");

    if (unit.getAni() != null && !unit.getAni().isEmpty()) {
      table.addCell(pdfService.getMessage("label.unit.ani", null, locale) + ":");
      table.addCell(unit.getAni());
    }
    table.completeRow();

    table.addCell(pdfService.getMessage("label.unit.position", null, locale) + ":");
    table.addCell(Point.isEmpty(unit.getPosition()) ? "N/A" : unit.getPosition().getInfo());
    table.addCell("");

    table.addCell(pdfService.getMessage("label.unit.home", null, locale) + ":");
    table.addCell(Point.isEmpty(unit.getHome()) ? "N/A" : unit.getHome().getInfo());
    table.addCell("");

    if (unit.getInfo() != null && !unit.getInfo().isEmpty()) {
      table.addCell(pdfService.getMessage("label.unit.info", null, locale) + ":");
      table.addCell(unit.getInfo());
      table.addCell("");
    }

    table.addCell(pdfService.getMessage("label.unit.withdoc", null, locale) + ":");
    table.addCell(pdfService.getMessage(unit.isWithDoc() ? "label.yes" : "label.no", null, locale));
    table.addCell("");

    table.addCell(pdfService.getMessage("label.unit.portable", null, locale) + ":");
    table.addCell(pdfService.getMessage(unit.isPortable() ? "label.yes" : "label.no", null, locale));
    table.addCell("");

    table.addCell(pdfService.getMessage("label.unit.vehicle", null, locale) + ":");
    table.addCell(pdfService.getMessage(unit.isTransportVehicle() ? "label.yes" : "label.no", null, locale));
    table.addCell("");

    table.completeRow();

    return table;
  }

  private Element printUnitLog(Unit unit) {
    List<LogEntry> logs = pdfService.getLogByUnitId(unit.getId());
    Collections.reverse(logs);

    PdfPTable table = new PdfPTable(new float[]{2, 2, 4, 3, 1, 5});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

    table.addCell(pdfService.getMessage("label.log.timestamp", null, locale));
    table.addCell(pdfService.getMessage("label.operator", null, locale));
    table.addCell(pdfService.getMessage("label.log.text", null, locale));
    table.addCell(pdfService.getMessage("label.incident", null, locale));
    table.addCell(pdfService.getMessage("label.task.state", null, locale));
    table.addCell(pdfService.getMessage("label.log.changes", null, locale));
    for (LogEntry log : logs) {
      table.addCell(getFormattedTime(log.getTimestamp()));
      table.addCell(log.getUser().getUsername());
      table.addCell(getLogText(log));
      table.addCell(getIncidentTitle(log.getIncident()));
      table.addCell(getTaskState(log.getState()));
      table.addCell(getLogChanges(log.getChanges()));
    }

    return table;
  }

  private Element printUnitIncidents(Unit unit) {
    if (unit.getIncidents() == null || unit.getIncidents().isEmpty()) {
      return null;
    }

    Paragraph p = new Paragraph(pdfService.getMessage("label.incidents", null, locale), PdfStyle.descrFont);

    PdfPTable table = new PdfPTable(new float[]{2, 3, 3, 1, 2});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

    table.addCell(pdfService.getMessage("label.incident", null, locale));
    table.addCell(pdfService.getMessage("label.incident.bo", null, locale) + "/" + pdfService.getMessage("label.incident.ao", null, locale));
    table.addCell(pdfService.getMessage("label.incident.info", null, locale));
    table.addCell(pdfService.getMessage("label.task.state", null, locale));
    table.addCell(pdfService.getMessage("label.last_update", null, locale));
    for (Map.Entry<Integer, TaskState> entry : unit.getIncidents().entrySet()) {
      Incident inc = pdfService.getIncidentById(entry.getKey());

      table.addCell(inc != null ? getIncidentTitle(inc) : "#" + entry.getKey());
      table.addCell(getBoAo(inc));
      table.addCell(inc != null ? inc.getInfo() : "");
      table.addCell(getTaskState(entry.getValue()));
      table.addCell(getFormattedTime(pdfService.getLastUpdate(entry.getKey(), unit.getId())));
    }

    p.add(table);
    return p;
  }

  private static void addEmptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(" "));
    }
  }

  private String getFormattedTime(Timestamp timestamp) {
    return new java.text.SimpleDateFormat(fullDate ? dateTimeFormat : timeFormat).format(timestamp);
  }

  private String getLogText(LogEntry log) {
    return log.getType() == LogEntryType.CUSTOM
            ? log.getText()
            : pdfService.getMessage("descr." + log.getType(), null, log.getText(), locale);
  }

  private String getIncidentTitle(Incident inc) {
    if (inc == null) {
      return "";
    }

    String title = "#" + inc.getId() + " - ";

    if (inc.getType() == IncidentType.Task) {
      if (inc.getBlue() == null || !inc.getBlue()) {
        title += pdfService.getMessage("label.incident.type.task", null, locale);
      } else {
        title += pdfService.getMessage("label.incident.type.task.blue", null, locale);
      }
    } else {
      title += pdfService.getMessage("label.incident.type." + inc.getType().toString().toLowerCase(), null, inc.getType().toString(), locale);
    }

    return title;
  }

  private PdfPTable getBoAo(Incident inc) {
    if (inc == null) {
      return null;
    }

    PdfPTable table = new PdfPTable(new float[]{1, 1});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    if (inc.getType() == IncidentType.Task || inc.getType() == IncidentType.Transport) {
      if (Point.isEmpty(inc.getAo())) {
        table.getDefaultCell().setColspan(2);
      }
      table.addCell(Point.isEmpty(inc.getBo())
              ? pdfService.getMessage("label.incident.nobo", null, locale)
              : inc.getBo().getInfo());
      if (!Point.isEmpty(inc.getAo())) {
        table.addCell(inc.getAo().getInfo());
      }
    } else {
      table.getDefaultCell().setColspan(2);
      table.addCell(Point.isEmpty(inc.getAo())
              ? pdfService.getMessage("label.incident.noao", null, locale)
              : inc.getAo().getInfo());
    }

    return table;
  }

  private String getUnitTitle(Unit unit) {
    return unit != null ? unit.getCall() + " - #" + unit.getId() : "";
  }

  private String getTaskState(TaskState state) {
    return state != null ? pdfService.getMessage("label.task.state." + state.toString().toLowerCase(), null, state.toString(), locale) : "";
  }

  private PdfPTable getLogChanges(JsonContainer changes) {
    if (changes == null || changes.getData() == null) {
      return null;
    }

    PdfPTable table = new PdfPTable(new float[]{1, 1, 1});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    for (Map.Entry<String, ChangePair<Object>> entry : changes.getData().entrySet()) {
      ChangePair value = entry.getValue();
      table.addCell(pdfService.getMessage("label." + changes.getType() + "." + entry.getKey(), null, entry.getKey(), locale) + ": ");
      table.addCell(value.getOldValue() != null ? value.getOldValue() + "" : "");
      table.addCell(value.getNewValue() != null ? value.getNewValue() + "" : "[empty]");
    }
    return table;
  }

}
