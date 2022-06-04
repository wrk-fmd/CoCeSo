package at.wrk.coceso.service.impl;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.PdfService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.utils.AuthenticatedUserProvider;
import at.wrk.coceso.utils.impl.PdfDocument;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
class PdfServiceImpl implements PdfService {

    private final static Logger LOG = LoggerFactory.getLogger(PdfServiceImpl.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LogService logService;

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private UnitService unitService;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    PdfServiceImpl(final AuthenticatedUserProvider authenticatedUserProvider) {
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    public void generateReport(
            final Concern concern,
            final boolean fullDate,
            final HttpServletResponse response,
            final Locale locale) {
        try (PdfDocument doc = new PdfDocument(PageSize.A4.rotate(), fullDate, this, messageSource, locale)) {
            addContentDispositionHeaderWithFilename(response, "coceso-report");

            doc.start(response);
            doc.addFrontPage("pdf.report.header", concern, authenticatedUserProvider.getAuthenticatedUser());
            doc.addStatistics(incidentService.getAll(concern));
            doc.addCustomLog(logService.getCustomAsc(concern));
            doc.addUnitsLog(unitService.getAllSorted(concern));
            doc.addIncidentsLog(incidentService.getAllForReport(concern));
            doc.addLastPage();

            LOG.info("PDF for concern {} completely written", concern);
        } catch (IOException | DocumentException e) {
            LOG.error("Error on printing pdf for concern {}", concern, e);
        }
    }

    @Override
    public void generateDump(
            final Concern concern,
            final boolean fullDate,
            final HttpServletResponse response,
            final Locale locale) {
        try (PdfDocument doc = new PdfDocument(PageSize.A4.rotate(), fullDate, this, messageSource, locale)) {
            addContentDispositionHeaderWithFilename(response, "coceso-dump");

            doc.start(response);
            doc.addFrontPage("pdf.dump.header", concern, authenticatedUserProvider.getAuthenticatedUser());
            doc.addUnitsCurrent(unitService.getAllSorted(concern));
            doc.addIncidentsCurrent(incidentService.getAllForDump(concern));
            doc.addLastPage();


            LOG.info("PDF for concern {} completely written", concern);
        } catch (IOException | DocumentException e) {
            LOG.error("Error on printing pdf for concern {}", concern, e);
        }
    }

    @Override
    public void generateTransport(
            final Concern concern,
            final boolean fullDate,
            final HttpServletResponse response,
            final Locale locale) {
        try (PdfDocument doc = new PdfDocument(PageSize.A4.rotate(), fullDate, this, messageSource, locale)) {
            addContentDispositionHeaderWithFilename(response, "coceso-transport-report");

            doc.start(response);
            doc.addFrontPage("pdf.transport.header", concern, authenticatedUserProvider.getAuthenticatedUser());
            doc.addTransports(incidentService.getAllTransports(concern));
            doc.addLastPage();

            LOG.info("PDF for concern {} completely written", concern);
        } catch (IOException | DocumentException e) {
            LOG.error("Error on printing pdf for concern {}", concern, e);
        }
    }

    @Override
    public void generatePatients(
            final Concern concern,
            final HttpServletResponse response,
            final Locale locale) {
        try (final PdfDocument doc = new PdfDocument(PageSize.A4.rotate(), false, this, messageSource, locale)) {
            addContentDispositionHeaderWithFilename(response, "coceso-patient-report");

            doc.start(response);
            doc.addFrontPage("pdf.patients.header", concern, authenticatedUserProvider.getAuthenticatedUser());
            doc.addPatients(patientService.getAllSorted(concern));
            doc.addLastPage();

            LOG.info("PDF for concern {} completely written", concern);
        } catch (IOException | DocumentException e) {
            LOG.error("Error on printing pdf for concern {}", concern, e);
        }
    }

    @Override
    public List<LogEntry> getLogByIncident(final Incident incident) {
        return logService.getByIncidentAsc(incident);
    }

    @Override
    public List<LogEntry> getLogByUnit(final Unit unit) {
        return logService.getByUnitAsc(unit);
    }

    @Override
    public Map<Unit, TaskState> getRelatedUnits(final Incident incident) {
        return unitService.getRelated(incident);
    }

    @Override
    public Timestamp getLastUpdate(final Incident incident, final Unit unit) {
        return logService.getLastTaskStateUpdate(incident, unit);
    }

    private void addContentDispositionHeaderWithFilename(final HttpServletResponse response, final String reportTypeFilenameSuffix) {
        String filename = String.format("%s_%s.pdf", DateTimeFormatter.ISO_DATE.format(LocalDate.now()), reportTypeFilenameSuffix);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=" + filename + "");
    }
}
