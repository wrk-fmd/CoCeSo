package at.wrk.coceso.utils.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.enums.IncidentType;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.Changes;
import at.wrk.coceso.entity.point.Point;
import at.wrk.coceso.service.PdfService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PdfDocument extends Document implements AutoCloseable {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
    private static final Font SUB_TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18);
    private static final Font TITLE_2_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font DESCRIPTION_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font DEFAULT_FONT = new Font(Font.FontFamily.HELVETICA, 10);

    private final static String TIME_FORMAT = "HH:mm:ss";
    private final static String DATE_TIME_FORMAT = "dd.MM.yy HH:mm:ss";

    private final PdfService pdfService;
    private final MessageSource messageSource;
    private final Locale locale;
    private final boolean fullDate;

    public PdfDocument(
            final Rectangle pageSize,
            final boolean fullDate,
            final PdfService pdfService,
            final MessageSource messageSource,
            final Locale locale) {
        super(pageSize);
        this.fullDate = fullDate;
        this.pdfService = pdfService;
        this.messageSource = messageSource;
        this.locale = locale;
    }

    public void start(final HttpServletResponse response) throws DocumentException, IOException {
        PdfWriter.getInstance(this, response.getOutputStream());
        this.open();
    }

    public void addFrontPage(final String titleMessageCode, final Concern concern, final User user) throws DocumentException {
        String title = getMessage(titleMessageCode, new String[]{concern.getName()}, titleMessageCode);

        this.addTitle(title);
        this.addAuthor(getMessage("coceso", null));
        this.addCreator(String.format("%s - %s", getMessage("coceso", null), user.getUsername()));

        Paragraph p = new Paragraph();
        addEmptyLine(p, 1);

        Paragraph p0 = new Paragraph(title, TITLE_FONT);
        p0.setAlignment(Element.ALIGN_CENTER);
        p.add(p0);
        addEmptyLine(p, 1);

        Paragraph p1 = new Paragraph(getMessage("pdf.created",
                new String[]{user.getFirstname(), user.getLastname(), new java.text.SimpleDateFormat(DATE_TIME_FORMAT).format(new Date())}), SUB_TITLE_FONT);
        p1.setAlignment(Element.ALIGN_CENTER);
        p.add(p1);

        if (!concern.getInfo().trim().isEmpty()) {
            addEmptyLine(p, 3);
            p.add(new Paragraph(getMessage("pdf.infos", new String[]{concern.getInfo()})));
        }

        this.add(p);
        this.newPage();
    }

    public void addLastPage() throws DocumentException {
        this.add(new Paragraph(getMessage("pdf.complete",
                new String[]{new java.text.SimpleDateFormat(DATE_TIME_FORMAT).format(new Date())})));
    }

    public void addStatistics(final List<Incident> incidents) throws DocumentException {
        int task, taskBlue, transport, transportBlue, relocation, relocationBlue, other, otherBlue;
        task = taskBlue = transport = transportBlue = relocation = relocationBlue = other = otherBlue = 0;

        for (Incident incident : incidents) {
            switch (incident.getType()) {
                case Task:
                    task++;
                    if (incident.isBlue()) {
                        taskBlue++;
                    }
                    break;
                case Transport:
                    transport++;
                    if (incident.isBlue()) {
                        transportBlue++;
                    }
                    break;
                case Relocation:
                    relocation++;
                    if (incident.isBlue()) {
                        relocationBlue++;
                    }
                    break;
                default:
                    other++;
                    if (incident.isBlue()) {
                        otherBlue++;
                    }
                    break;
            }
        }

        PdfPTable table = new PdfPTable(new float[]{2, 1, 1});
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
        table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

        addCell(table, "");
        addCell(table, getMessage("pdf.report.total", null));
        addCell(table, getMessage("pdf.report.stat_blue", null));

        addCell(table, getMessage("incident.type.task", null) + " / " + getMessage("incident.type.task.blue", null));
        addCell(table, "" + task);
        addCell(table, "" + taskBlue);

        addCell(table, getMessage("incident.type.transport", null));
        addCell(table, "" + transport);
        addCell(table, "" + transportBlue);

        addCell(table, getMessage("incident.type.relocation", null));
        addCell(table, "" + relocation);
        addCell(table, "" + relocationBlue);

        addCell(table, getMessage("pdf.report.incident.other", null));
        addCell(table, "" + other);
        addCell(table, "" + otherBlue);

        this.add(new Paragraph(getMessage("pdf.report.statistics", null), TITLE_FONT));
        this.add(new Paragraph(""));
        this.add(table);

        this.add(new Paragraph(""));
        this.newPage();
    }

    public void addCustomLog(final List<LogEntry> logs) throws DocumentException {
        Paragraph p = new Paragraph();
        Paragraph h = new Paragraph(getMessage("log.custom", null), TITLE_2_FONT);
        p.add(h);

        PdfPTable table = new PdfPTable(new float[]{2, 2, 7.5F, 2, 2.5F});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

        addCell(table, getMessage("log.timestamp", null));
        addCell(table, getMessage("user", null));
        addCell(table, getMessage("log.text", null));
        addCell(table, getMessage("unit", null));
        addCell(table, getMessage("incident", null));

        logs.forEach(log -> {
            addCell(table, getFormattedTime(log.getTimestamp()));
            addCell(table, log.getUsername());
            addCell(table, log.getText());
            addCell(table, getUnitTitle(log.getUnit()));
            addCell(table, getIncidentTitle(log.getIncident()));
        });

        p.add(table);
        this.add(p);
        this.newPage();
    }

    /**
     * Add log entries and details for all non-single incidents (for final report)
     */
    public void addIncidentsLog(List<Incident> incidents) throws DocumentException {
        this.add(new Paragraph(getMessage("incidents", null), TITLE_FONT));
        this.add(new Paragraph(" "));

        for (Incident incident : incidents) {
            Paragraph p = printIncident(incident);
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
     */
    public void addTransports(List<Incident> incidents) throws DocumentException {
        this.add(new Paragraph(getMessage("pdf.transport", null), TITLE_FONT));
        this.add(new Paragraph(" "));

        for (Incident incident : incidents) {
            Paragraph p = printIncident(incident);
            p.add(printRelatedUnits(incident));
            addEmptyLine(p, 1);
            this.add(p);
        }
        this.newPage();
    }

    /**
     * Get current state of not-done incidents (for dump)
     */
    public void addIncidentsCurrent(List<Incident> incidents) throws DocumentException {
        this.add(new Paragraph(getMessage("incidents", null), TITLE_FONT));
        this.add(new Paragraph(" "));

        for (Incident incident : incidents) {
            Paragraph p = printIncident(incident);
            p.add(printIncidentUnits(incident));
            addEmptyLine(p, 1);
            this.add(p);
        }
        this.newPage();
    }

    /**
     * Add log entries for all units (final report)
     */
    public void addUnitsLog(List<Unit> units) throws DocumentException {
        this.add(new Paragraph(getMessage("units", null), TITLE_FONT));
        this.add(new Paragraph(" "));

        for (Unit unit : units) {
            Paragraph p = printUnitTitle(unit);
            p.add(printUnitLog(unit));
            addEmptyLine(p, 1);
            this.add(p);
        }
        this.newPage();
    }

    /**
     * Add unit details and assigned incidents (current state for dump)
     */
    public void addUnitsCurrent(List<Unit> units) throws DocumentException {
        this.add(new Paragraph(getMessage("units", null), TITLE_FONT));
        this.add(new Paragraph(" "));

        for (Unit unit : units) {
            Paragraph p = printUnitTitle(unit);
            p.add(printUnitDetails(unit));
            addEmptyLine(p, 1);
            p.add(printUnitIncidents(unit));
            addEmptyLine(p, 1);
            this.add(p);
        }
        this.newPage();
    }

    /**
     * Add all transports (for transport list).
     */
    public void addPatients(final List<Patient> patients) throws DocumentException {
        this.add(new Paragraph(getMessage("pdf.patients", null), TITLE_FONT));
        this.add(new Paragraph(" "));

        Paragraph p = new Paragraph();
        PdfPTable table = new PdfPTable(new float[]{1, 2, 2, 2, 1, 2, 1, 3, 3, 2});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

        addCell(table, getMessage("patient.id", null));
        addCell(table, getMessage("patient.externalId", null));
        addCell(table, getMessage("patient.lastname", null));
        addCell(table, getMessage("patient.firstname", null));
        addCell(table, getMessage("patient.insurance", null));
        addCell(table, getMessage("patient.birthday", null));
        addCell(table, getMessage("patient.naca", null));
        addCell(table, getMessage("patient.diagnosis", null));
        addCell(table, getMessage("patadmin.hospital", null));
        addCell(table, getMessage("incident.casus.short", null));

        patients.forEach(patient -> {
            addCell(table, patient.getId().toString());
            addCell(table, patient.getExternalId());
            addCell(table, patient.getLastname());
            addCell(table, patient.getFirstname());
            addCell(table, patient.getInsurance());
            addCell(table, patient.getBirthday() == null ? "" : patient.getBirthday().format(DateTimeFormatter.ISO_DATE));
            addCell(table, patient.getNaca() == null ? "" : patient.getNaca().name());
            addCell(table, patient.getDiagnosis());

            Set<String> hospital = patient.getHospital();
            if (!hospital.isEmpty()) {
                addCell(table, String.join("\n", hospital));
            } else {
                addCell(table, patient.isDone() ? getMessage("patient.discharged", null) : "");
            }

            String casusNr = patient.getIncidents().stream().map(Incident::getCasusNr).filter(StringUtils::isNotBlank).findFirst().orElse("");
            addCell(table, casusNr);
        });

        p.add(table);
        this.add(p);
        this.newPage();
    }

    private Paragraph printIncident(final Incident inc) {
        Paragraph p = new Paragraph();
        p.add(new Paragraph(getIncidentTitle(inc), TITLE_2_FONT));

        p.add(printIncidentDetails(inc));
        addEmptyLine(p, 1);
        if (inc.getPatient() != null) {
            p.add(printPatientDetails(inc.getPatient()));
            addEmptyLine(p, 1);
        }

        return p;
    }

    private Element printIncidentDetails(Incident inc) {
        PdfPTable table = new PdfPTable(new float[]{2, 4, 1, 2, 4, 0});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        addCell(table, getMessage("incident.blue", null) + ":");
        addCell(table, getMessage(inc.isBlue() ? "yes" : "no", null));
        addCell(table, "");

        addCell(table, getMessage("incident.state", null) + ":");
        addCell(table, getMessage("incident.state." + inc.getState().toString().toLowerCase(), null, inc.getState().toString()));
        addCell(table, "");

        if (inc.getType() == IncidentType.Task || inc.getType() == IncidentType.Transport) {
            addCell(table, getMessage("incident.bo", null) + ":");
            addCell(table, Point.isEmpty(inc.getBo())
                    ? getMessage("incident.nobo", null)
                    : inc.getBo().getInfo());
            addCell(table, "");
        }

        addCell(table, getMessage("incident.ao", null) + ":");
        addCell(table, Point.isEmpty(inc.getAo())
                ? getMessage("incident.noao", null)
                : inc.getAo().getInfo());
        addCell(table, "");

        if (inc.getInfo() != null && !inc.getInfo().isEmpty()) {
            addCell(table, getMessage("incident.info", null) + ":");
            addCell(table, inc.getInfo());
        }
        table.completeRow();

        if (inc.getCaller() != null && !inc.getCaller().isEmpty()) {
            addCell(table, getMessage("incident.caller", null) + ":");
            addCell(table, inc.getCaller());
            addCell(table, "");
        }

        if (inc.getCasusNr() != null && !inc.getCasusNr().isEmpty()) {
            addCell(table, getMessage("incident.casus", null) + ":");
            addCell(table, inc.getCasusNr());
            addCell(table, "");
        }
        table.completeRow();

        return table;
    }

    private Element printPatientDetails(Patient patient) {
        PdfPTable table = new PdfPTable(new float[]{2, 4, 1, 2, 4, 0});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        addCell(table, getMessage("patient", null) + ":");
        addCell(table, patient.getFirstname() + " " + patient.getLastname());
        addCell(table, "");

        addCell(table, getMessage("patient.sex", null) + ":");
        addCell(table, patient.getSex() == null
                ? getMessage("patient.sex.u", null)
                : getMessage("patient.sex.long." + patient.getSex().toString().toLowerCase(), null, patient.getSex().toString()));
        addCell(table, "");

        if (patient.getInsurance() != null && !patient.getInsurance().isEmpty()) {
            addCell(table, getMessage("patient.insurance", null) + ":");
            addCell(table, patient.getInsurance());
            addCell(table, "");
        }

        if (patient.getExternalId() != null && !patient.getExternalId().isEmpty()) {
            addCell(table, getMessage("patient.externalId", null) + ":");
            addCell(table, patient.getExternalId());
            addCell(table, "");
        }
        table.completeRow();

        if (patient.getDiagnosis() != null && !patient.getDiagnosis().isEmpty()) {
            addCell(table, getMessage("patient.diagnosis", null) + ":");
            addCell(table, patient.getDiagnosis());
            addCell(table, "");
        }

        if (patient.getErtype() != null && !patient.getErtype().isEmpty()) {
            addCell(table, getMessage("patient.ertype", null) + ":");
            addCell(table, patient.getErtype());
            addCell(table, "");
        }
        table.completeRow();

        if (patient.getInfo() != null && !patient.getInfo().isEmpty()) {
            addCell(table, getMessage("patient.info", null) + ":");
            addCell(table, patient.getInfo());
        }
        table.completeRow();

        return table;
    }

    private Element printIncidentLog(Incident inc) {
        List<LogEntry> logs = pdfService.getLogByIncident(inc);

        Paragraph p = new Paragraph(getMessage("log", null), DESCRIPTION_FONT);

        PdfPTable table = new PdfPTable(new float[]{2, 2, 4, 2, 1, 5});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

        printUnitOrIndicentHeader(table, "unit");
        logs.forEach(log -> printUnitOrIncidentRow(table, log, getUnitTitle(log.getUnit())));

        p.add(table);
        return p;
    }

    private Element printIncidentUnits(Incident inc) {
        Map<Unit, TaskState> units = inc.getUnits();
        return printUnits(inc, units);
    }

    private Element printRelatedUnits(Incident inc) {
        Map<Unit, TaskState> units = pdfService.getRelatedUnits(inc);
        return printUnits(inc, units);
    }

    private Element printUnits(final Incident inc, final Map<Unit, TaskState> units) {
        if (units == null || units.isEmpty()) {
            return null;
        }

        Paragraph p = new Paragraph(getMessage("units", null), DESCRIPTION_FONT);

        PdfPTable table = new PdfPTable(new float[]{2, 1, 2});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

        addCell(table, getMessage("unit", null));
        addCell(table, getMessage("task.state", null));
        addCell(table, getMessage("last_change", null));
        units.forEach((unit, state) -> {
            addCell(table, getUnitTitle(unit));
            addCell(table, getTaskState(state));
            addCell(table, getFormattedTime(pdfService.getLastUpdate(inc, unit)));
        });

        p.add(table);
        return p;
    }

    private Paragraph printUnitTitle(Unit unit) {
        Paragraph p = new Paragraph();
        p.add(new Paragraph(unit.getCall() + " - #" + unit.getId(), TITLE_2_FONT));
        return p;
    }

    private Element printUnitDetails(Unit unit) {
        PdfPTable table = new PdfPTable(new float[]{2, 4, 1, 2, 4, 0});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        addCell(table, getMessage("unit.state", null) + ":");
        addCell(table, getMessage("unit.state." + unit.getState().toString().toLowerCase(), null, unit.getState().toString()));
        addCell(table, "");

        if (unit.getAni() != null && !unit.getAni().isEmpty()) {
            addCell(table, getMessage("unit.ani", null) + ":");
            addCell(table, unit.getAni());
        }
        table.completeRow();

        addCell(table, getMessage("unit.position", null) + ":");
        addCell(table, Point.isEmpty(unit.getPosition()) ? "N/A" : unit.getPosition().getInfo());
        addCell(table, "");

        addCell(table, getMessage("unit.home", null) + ":");
        addCell(table, Point.isEmpty(unit.getHome()) ? "N/A" : unit.getHome().getInfo());
        addCell(table, "");

        if (unit.getInfo() != null && !unit.getInfo().isEmpty()) {
            addCell(table, getMessage("unit.info", null) + ":");
            addCell(table, unit.getInfo());
            addCell(table, "");
        }

        addCell(table, getMessage("unit.withdoc", null) + ":");
        addCell(table, getMessage(unit.isWithDoc() ? "yes" : "no", null));
        addCell(table, "");

        addCell(table, getMessage("unit.portable", null) + ":");
        addCell(table, getMessage(unit.isPortable() ? "yes" : "no", null));
        addCell(table, "");

        addCell(table, getMessage("unit.vehicle", null) + ":");
        addCell(table, getMessage(unit.isTransportVehicle() ? "yes" : "no", null));
        addCell(table, "");

        table.completeRow();

        return table;
    }

    private Element printUnitLog(Unit unit) {
        List<LogEntry> logs = pdfService.getLogByUnit(unit);

        PdfPTable table = new PdfPTable(new float[]{2, 2, 3.5F, 2.5F, 1, 5});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

        printUnitOrIndicentHeader(table, "incident");
        logs.forEach(log -> printUnitOrIncidentRow(table, log, getIncidentTitle(log.getIncident())));

        return table;
    }

    private void printUnitOrIncidentRow(final PdfPTable table, final LogEntry log, final String titleOfRowEntity) {
        addCell(table, getFormattedTime(log.getTimestamp()));
        addCell(table, log.getUsername());
        addCell(table, getLogText(log));
        addCell(table, titleOfRowEntity);
        addCell(table, getTaskState(log.getState()));
        table.addCell(getLogChanges(log.getChanges()));
    }

    private void printUnitOrIndicentHeader(final PdfPTable table, final String headerTypeMessageCode) {
        addCell(table, getMessage("log.timestamp", null));
        addCell(table, getMessage("user", null));
        addCell(table, getMessage("log.text", null));
        addCell(table, getMessage(headerTypeMessageCode, null));
        addCell(table, getMessage("task.state", null));
        addCell(table, getMessage("log.changes", null));
    }

    private Element printUnitIncidents(Unit unit) {
        if (unit.getIncidents() == null || unit.getIncidents().isEmpty()) {
            return null;
        }

        Paragraph p = new Paragraph(getMessage("incidents", null), DESCRIPTION_FONT);

        PdfPTable table = new PdfPTable(new float[]{2, 3, 3, 1, 2});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.BOTTOM);

        addCell(table, getMessage("incident", null));
        addCell(table, getMessage("incident.bo", null) + "/" + getMessage("incident.ao", null));
        addCell(table, getMessage("incident.info", null));
        addCell(table, getMessage("task.state", null));
        addCell(table, getMessage("last_change", null));
        unit.getIncidents().forEach((inc, s) -> {
            addCell(table, getIncidentTitle(inc));
            table.addCell(getBoAo(inc));
            addCell(table, inc.getInfo());
            addCell(table, getTaskState(s));
            addCell(table, getFormattedTime(pdfService.getLastUpdate(inc, unit)));
        });

        p.add(table);
        return p;
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private String getFormattedTime(Timestamp timestamp) {
        return timestamp == null ? "" : new SimpleDateFormat(fullDate ? DATE_TIME_FORMAT : TIME_FORMAT).format(timestamp);
    }

    private String getLogText(LogEntry log) {
        return log.getType() == LogEntryType.CUSTOM
                ? log.getText()
                : getMessage("log.type." + log.getType(), null, log.getText());
    }

    private String getIncidentTitle(Incident inc) {
        if (inc == null) {
            return "";
        }

        String title = "#" + inc.getId() + " - ";

        if (inc.getType() == IncidentType.Task) {
            if (!inc.isBlue()) {
                title += getMessage("incident.type.task", null);
            } else {
                title += getMessage("incident.type.task.blue", null);
            }
        } else {
            title += getMessage("incident.type." + inc.getType().toString().toLowerCase(), null, inc.getType().toString());
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
            addCell(table, Point.isEmpty(inc.getBo())
                    ? getMessage("incident.nobo", null)
                    : inc.getBo().getInfo());
            if (!Point.isEmpty(inc.getAo())) {
                addCell(table, inc.getAo().getInfo());
            }
        } else {
            table.getDefaultCell().setColspan(2);
            addCell(table, Point.isEmpty(inc.getAo())
                    ? getMessage("incident.noao", null)
                    : inc.getAo().getInfo());
        }

        return table;
    }

    private String getUnitTitle(Unit unit) {
        return unit != null ? unit.getCall() + " - #" + unit.getId() : "";
    }

    private String getTaskState(TaskState state) {
        return state != null ? getMessage("task.state." + state.toString().toLowerCase(), null, state.toString()) : "";
    }

    private PdfPTable getLogChanges(Changes changes) {
        if (changes == null) {
            return null;
        }

        PdfPTable table = new PdfPTable(new float[]{1, 1, 1});
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.setPaddingTop(-2);

        changes.forEach(c -> {
            addCell(table, getMessage(changes.getType() + "." + c.getKey().toLowerCase(), null, c.getKey()) + ": ");
            addCell(table, c.getOldValue() != null ? c.getOldValue() + "" : "");
            addCell(table, c.getNewValue() != null ? c.getNewValue() + "" : "[empty]");
        });

        return table;
    }

    private void addCell(PdfPTable table, String text) {
        table.addCell(new Phrase(text, DEFAULT_FONT));
    }

    private String getMessage(String code, Object[] args) throws NoSuchMessageException {
        return messageSource.getMessage(code, args, locale);
    }

    private String getMessage(final String code, final Object[] args, final String defaultText) {
        return messageSource.getMessage(code, args, defaultText, locale);
    }
}
