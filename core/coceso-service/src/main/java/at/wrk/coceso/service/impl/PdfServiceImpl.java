package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.JournalEntry;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.JournalService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.PdfService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.utils.AuthenticatedUser;
import at.wrk.coceso.utils.impl.PdfDocument;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@Transactional
class PdfServiceImpl implements PdfService {

    private final MessageSource messageSource;
    private final JournalService journalService;
    private final IncidentService incidentService;
    private final PatientService patientService;
    private final UnitService unitService;

    @Autowired
    public PdfServiceImpl(final MessageSource messageSource, final JournalService journalService, final IncidentService incidentService,
            final PatientService patientService, final UnitService unitService) {
        this.messageSource = messageSource;
        this.journalService = journalService;
        this.incidentService = incidentService;
        this.patientService = patientService;
        this.unitService = unitService;
    }

    @Override
    public void generateReport(final Concern concern, final boolean fullDate, final HttpServletResponse response, final Locale locale) {
        try (PdfDocument doc = new PdfDocument(PageSize.A4.rotate(), fullDate, this, messageSource, locale)) {
            doc.start(response);
            doc.addFrontPage("pdf.report.header", concern, AuthenticatedUser.getName());
            doc.addStatistics(incidentService.getAll(concern));
            doc.addCustomLog(journalService.getCustomAsc(concern));
            doc.addUnitsLog(unitService.getAllSorted(concern));
            doc.addIncidentsLog(incidentService.getAllForReport(concern));
            doc.addLastPage();

            log.info("PDF for concern {} completely written", concern);
        } catch (IOException | DocumentException e) {
            log.error("Error on printing pdf for concern {}", concern, e);
        }
    }

    @Override
    public void generateDump(final Concern concern, final boolean fullDate, final HttpServletResponse response, final Locale locale) {
        try (PdfDocument doc = new PdfDocument(PageSize.A4.rotate(), fullDate, this, messageSource, locale)) {
            doc.start(response);
            doc.addFrontPage("pdf.dump.header", concern, AuthenticatedUser.getName());
            doc.addUnitsCurrent(unitService.getAllSorted(concern));
            doc.addIncidentsCurrent(incidentService.getAllForDump(concern));
            doc.addLastPage();

            log.info("PDF for concern {} completely written", concern);
        } catch (IOException | DocumentException e) {
            log.error("Error on printing pdf for concern {}", concern, e);
        }
    }

    @Override
    public void generateTransport(final Concern concern, final boolean fullDate, final HttpServletResponse response, final Locale locale) {
        try (PdfDocument doc = new PdfDocument(PageSize.A4.rotate(), fullDate, this, messageSource, locale)) {
            doc.start(response);
            doc.addFrontPage("pdf.transport.header", concern, AuthenticatedUser.getName());
            doc.addTransports(incidentService.getAllTransports(concern));
            doc.addLastPage();

            log.info("PDF for concern {} completely written", concern);
        } catch (IOException | DocumentException e) {
            log.error("Error on printing pdf for concern {}", concern, e);
        }
    }

    @Override
    public void generatePatients(final Concern concern, final HttpServletResponse response, final Locale locale) {
        try (final PdfDocument doc = new PdfDocument(PageSize.A4.rotate(), false, this, messageSource, locale)) {
            doc.start(response);
            doc.addFrontPage("pdf.patients.header", concern, AuthenticatedUser.getName());
            doc.addPatients(patientService.getAllSorted(concern));
            doc.addLastPage();

            log.info("PDF for concern {} completely written", concern);
        } catch (IOException | DocumentException e) {
            log.error("Error on printing pdf for concern {}", concern, e);
        }
    }

    @Override
    public List<JournalEntry> getLogByIncident(final Incident incident) {
        return journalService.getByIncidentAsc(incident);
    }

    @Override
    public List<JournalEntry> getLogByUnit(final Unit unit) {
        return journalService.getByUnitAsc(unit);
    }

    @Override
    public List<Task> getRelatedUnits(final Incident incident) {
        return unitService.getRelated(incident);
    }

    @Override
    public Instant getLastUpdate(final Incident incident, final Unit unit) {
        return journalService.getLastTaskStateUpdate(incident, unit);
    }
}
