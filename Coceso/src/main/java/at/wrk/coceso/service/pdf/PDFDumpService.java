package at.wrk.coceso.service.pdf;

import at.wrk.coceso.entity.*;
import at.wrk.coceso.entity.enums.IncidentState;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.enums.UnitState;
import at.wrk.coceso.entity.helper.JsonContainer;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.utils.Logger;
import at.wrk.coceso.utils.PdfStyle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

@Service
public class PDFDumpService {
    private boolean initialized = false;

    private String dateFormat;
    private Locale locale;
    private Concern concern;
    private Operator user;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LogService logService;

    @Autowired
    UnitService unitService;

    @Autowired
    IncidentService incidentService;

    @Autowired
    PatientService patientService;

    private java.util.Map<Integer, Unit> unitMap;
    private java.util.Map<Integer, Incident> incidentMap;


    public void init(String dateFormat, Locale locale, Concern concern, Operator user) {
        this.dateFormat = dateFormat;
        this.locale = locale;
        this.concern = concern;
        this.user = user;

        initialized = true;
    }

    public void create(Document document) throws DocumentException {
        if(!initialized) {
            return;
        }

        unitMap = new HashMap<Integer, Unit>();
        for(Unit unit : unitService.getAll(concern.getId())) {
            unitMap.put(unit.getId(), unit);
        }

        incidentMap = new HashMap<Integer, Incident>();
        for(Incident incident : incidentService.getAll(concern.getId())) {
            incidentMap.put(incident.getId(), incident);
        }


        addFrontPage(document);
        addUnitStats(document);
        addIncidentStats(document);
        addLastPage(document);

    }

    private void addUnitStats(Document document) throws DocumentException {
        ObjectMapper mapper = new ObjectMapper();

        document.add(new Paragraph(messageSource.getMessage("label.units", null, locale), PdfStyle.titleFont));
        document.add(new Paragraph(" "));

        // Sort alphabetically by 'call'
        List<Unit> unitList = new ArrayList<Unit>(unitMap.values());
        Collections.sort(unitList, new Comparator<Unit>() {
            @Override
            public int compare(Unit o1, Unit o2) {
                if(o1 == null || o1.getCall() == null) {
                    return 1;
                }
                return o1.getCall().compareTo(o2.getCall());
            }
        });

        for(Unit unit : unitList) {
            java.util.List<LogEntry> logs = logService.getByUnitId(unit.getId());

            Timestamp stateChange = null;
            UnitState current = unit.getState();

            // Find Timestamp of last State Change
            Iterator<LogEntry> iterator = logs.iterator();
            LogEntry last = null;
            while(current != null && iterator.hasNext() && stateChange == null) {
                LogEntry log = iterator.next();

                if(log.getJson() != null) {
                    JsonContainer jsonContainer;

                    try {
                        jsonContainer = mapper.readValue(log.getJson(), JsonContainer.class);
                        Unit tUnit = jsonContainer.getUnit();

                        if( log != null && tUnit.getState() != null ) {
                            if(tUnit.getState() != current) {
                                // No valid LogEntry with same State found before
                                if(last == null) {
                                    Logger.warning("PDFDumpService.addUnitStats(): CORRUPT DATA while parsing last UnitState Change!");
                                    break;
                                }
                                // Set Timestamp of last LogEntry with same State as 'current'
                                stateChange = last.getTimestamp();
                            } else {
                                // Save last LogEntry with same State as 'current'
                                last = log;
                            }
                        }
                    } catch (IOException e) {
                        Logger.warning(e.getMessage());
                    }
                }
            }

            Paragraph p = new Paragraph();

            Paragraph h = new Paragraph(unit.getCall() + " - #" + unit.getId(), PdfStyle.title2Font);

            // Unit information
            Paragraph s = new Paragraph((unit.getAni() == null || unit.getAni().isEmpty() ? "" : ("ANI: " + unit.getAni()) + "\n") +
                    messageSource.getMessage("label.unit.home", null, locale) + ": " + (unit.getHome() == null ? "N/A" : unit.getHome()) + "\n" +
                    messageSource.getMessage("label.unit.position", null, locale) + ": " + (unit.getPosition() == null ? "N/A" : unit.getPosition()) + "\n" +
                    messageSource.getMessage("label.unit.state", null, locale) + ": " +
                    (current == null ? "--" : current + " (" + (stateChange == null ? "--" : new java.text.SimpleDateFormat(dateFormat).format(stateChange) ) + ")" ) );
            p.add(h);
            p.add(s);

            if(unit.getIncidents().size() > 0) {
                // Table for Incidents
                PdfPTable table = new PdfPTable(new float[]{0.3f, 0.5f, 1, 1, 1, 0.4f, 0.7f});
                table.setWidthPercentage(100);

                table.addCell("ID");
                table.addCell(messageSource.getMessage("label.incident.type", null, locale));
                table.addCell(messageSource.getMessage("label.incident.bo", null, locale));
                table.addCell(messageSource.getMessage("label.incident.ao", null, locale));
                table.addCell(messageSource.getMessage("label.incident.info", null, locale));
                table.addCell(messageSource.getMessage("label.task.state", null, locale));
                table.addCell(messageSource.getMessage("label.last_update", null, locale));


                // Incidents of Unit information
                for (Integer incidentID : unit.getIncidents().keySet()) {
                    Incident incident = incidentMap.get(incidentID);
                    if (incident != null) {
                        Timestamp lastChange = null;
                        TaskState state = unit.getIncidents().get(incidentID);

                        // Search Timestamp of last TaskState Change
                        for (LogEntry logEntry : logService.getByIncidentId(incidentID)) {
                            if (logEntry.getType() == LogEntryType.TASKSTATE_CHANGED && logEntry.getUnit() != null && logEntry.getUnit().getId() == unit.getId()) {
                                if (logEntry.getState() == state) {
                                    lastChange = logEntry.getTimestamp();
                                } else {
                                    Logger.warning("PDFDumpService.addUnitStats(): CORRUPT DATA while parsing last TaskState Change!");
                                }
                                break;
                            }
                        }

                        table.addCell(incidentID+"");
                        table.addCell(PdfStyle.humanreadableIncidentType(messageSource, locale, incident));
                        table.addCell(incident.getBo() == null ? "" : incident.getBo().getInfo());
                        table.addCell(incident.getAo() == null ? "" : incident.getAo().getInfo());
                        table.addCell(incident.getInfo() == null ? "" : incident.getInfo());
                        table.addCell(state == null ? "N/A" : messageSource.getMessage("label.task.state." + state.name().toLowerCase(), null, locale));
                        table.addCell(lastChange == null ? "N/A" : new java.text.SimpleDateFormat(dateFormat).format(lastChange));
                    } else {
                        Logger.warning("PDFDumpService.addUnitStats(): assigned Incident with ID #" + incidentID + " not found.");
                    }

                }
                p.add(table);
            }
            // Empty Line
            p.add(new Paragraph());

            document.add(p);
        }
        document.newPage();
    }

    private void addIncidentStats(Document document) throws DocumentException {
        ObjectMapper mapper = new ObjectMapper();

        document.add(new Paragraph(messageSource.getMessage("label.incidents", null, locale), PdfStyle.titleFont));
        document.add(new Paragraph(" "));

        for(Incident incident : incidentMap.values()) {
            // Single Unit Incidents are fully logged by UnitStats
            // Incidents of State 'Done' not relevant here
            if(incident.getType().isSingleUnit() || incident.getState() == IncidentState.Done)
                continue;

            Paragraph p = new Paragraph();

            Paragraph h = new Paragraph("#" + incident.getId() + " - " + PdfStyle.humanreadableIncidentType(messageSource, locale, incident),
                    PdfStyle.title2Font);

            Paragraph s = new Paragraph(messageSource.getMessage("label.incident.bo", null, locale) + ": " +
                    (incident.getBo() == null ? "N/A" : incident.getBo()) + "\n" +

                    messageSource.getMessage("label.incident.ao", null, locale) + ": " +
                    (incident.getAo() == null ? "N/A" : incident.getAo()) + "\n" +

                    messageSource.getMessage("label.incident.info", null, locale) + ": " +
                    (incident.getInfo() == null ? "" : incident.getInfo()) + "\n" +

                    messageSource.getMessage("label.incident.caller", null, locale) + ": " +
                    (incident.getCaller() == null ? "" : incident.getCaller()) + "\n" +

                    messageSource.getMessage("label.incident.casus", null, locale) + ": " +
                    (incident.getCasusNr() == null ? "N/A" : incident.getCasusNr()) + "\n" +

                    messageSource.getMessage("label.incident.blue", null, locale) + ": " +
                    ( incident.getBlue() == null ? "-" : (incident.getBlue() ?
                            messageSource.getMessage("label.yes", null, locale) :
                            messageSource.getMessage("label.no", null, locale) ) ),

                    PdfStyle.descrFont);
            p.add(h);
            p.add(s);

            Patient patient = patientService.getById(incident.getId());
            if(patient != null) {
                Paragraph head = new Paragraph(messageSource.getMessage("label.patient", null, locale) + ":");

                Paragraph pat = new Paragraph(patient.getGiven_name() + " " + patient.getSur_name() + "\n" +
                        messageSource.getMessage("label.patient.sex", null, locale) + ": " + patient.getSex() + "\n" +
                        messageSource.getMessage("label.patient.insurance_number", null, locale) + ": " + patient.getInsurance_number() + "\n" +
                        messageSource.getMessage("label.patient.externalID", null, locale) + ": " + patient.getExternalID() + "\n" +
                        messageSource.getMessage("label.patient.diagnosis", null, locale) + ": " + patient.getDiagnosis() + "\n" +
                        messageSource.getMessage("label.patient.erType", null, locale) + ": " + patient.getErType() + "\n" +
                        messageSource.getMessage("label.patient.info", null, locale) + ": " + patient.getInfo() + "\n",
                        PdfStyle.descrFont);

                p.add(new Paragraph());
                p.add(head);
                p.add(pat);
            }

            p.add(new Paragraph());
            p.add(new Paragraph(messageSource.getMessage("label.main.unit.assigned", null, locale) +
                    ": (" + incident.getUnits().size() + ")"));

            for(Integer unitID : incident.getUnits().keySet()) {
                p.add(new Paragraph("- " + unitMap.get(unitID).getCall() + " (#" + unitID + ")", PdfStyle.descrFont));
            }

            // Empty Line
            p.add(new Paragraph(" "));
            document.add(p);
        }
        document.newPage();
    }

    private void addFrontPage(Document document) throws DocumentException {
        // TODO localization
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);

        Paragraph p0 = new Paragraph("PDF Dump der Ambulanz: " + concern.getName(), PdfStyle.titleFont);
        p0.setAlignment(Element.ALIGN_CENTER);
        preface.add(p0);

        addEmptyLine(preface, 1);

        Paragraph p1 = new Paragraph("Erstellt von: " + user.getGiven_name() + " " + user.getSur_name() +
                " am " + new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS").format(new Date()), PdfStyle.subTitleFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        preface.add(p1);

        addEmptyLine(preface, 3);
        if(!concern.getInfo().trim().isEmpty())
            preface.add(new Paragraph("Infos zur Ambulanz:\n" + concern.getInfo(), PdfStyle.defFont));

        document.add(preface);
        document.newPage();
    }

    private void addLastPage(Document document) throws DocumentException {
        document.add(new Paragraph("Dokument vollständig erstellt. " +
                new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS").format(new Date())));
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void setDestructed() {
        initialized = false;
    }
}
