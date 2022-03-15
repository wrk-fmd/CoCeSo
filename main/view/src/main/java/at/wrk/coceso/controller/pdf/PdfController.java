package at.wrk.coceso.controller.pdf;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.exceptions.ConcernException;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.PdfService;
import at.wrk.coceso.utils.AuthenticatedUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@PreAuthorize("@auth.hasAccessLevel('Report')")
@Controller
@RequestMapping("/pdf")
public class PdfController {

    private final static Logger LOG = LoggerFactory.getLogger(PdfController.class);

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ConcernService concernService;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    public PdfController(final AuthenticatedUserProvider authenticatedUserProvider) {
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @RequestMapping(value = "report", produces = "application/pdf", method = RequestMethod.GET)
    public void report(
            @RequestParam(value = "id") final int id,
            @RequestParam(value = "fullDate", defaultValue = "0") final boolean fullDate,
            final HttpServletResponse response,
            final Locale locale) throws ConcernException {
        Concern concern = concernService.getById(id);

        if (concern == null) {
            LOG.info("{}: Failed to read concern during PDF creation. Concern #{} does not exist in database.", authenticatedUserProvider.getAuthenticatedUser(), id);
            throw new ConcernException("Concern does not exist.");
        }

        LOG.info("{}: Requested final report for concern {}", authenticatedUserProvider.getAuthenticatedUser(), concern);

        pdfService.generateReport(concern, fullDate, response, locale);
    }

    @RequestMapping(value = "dump", produces = "application/pdf", method = RequestMethod.GET)
    public void dump(
            @RequestParam(value = "id") final int id,
            @RequestParam(value = "fullDate", defaultValue = "0") final boolean fullDate,
            final HttpServletResponse response,
            final Locale locale) throws ConcernException {
        Concern concern = concernService.getById(id);
        if (Concern.isClosedOrNull(concern)) {
            throw new ConcernException("Concern is already closed.");
        }

        LOG.info("{}: Requested pdf dump for concern {}", authenticatedUserProvider.getAuthenticatedUser(), concern);

        pdfService.generateDump(concern, fullDate, response, locale);
    }

    @RequestMapping(value = "transport", produces = "application/pdf", method = RequestMethod.GET)
    public void transport(
            @RequestParam(value = "id") final int id,
            @RequestParam(value = "fullDate", defaultValue = "0") final boolean fullDate,
            final HttpServletResponse response,
            final Locale locale) throws ConcernException {
        Concern concern = concernService.getById(id);
        if (concern == null) {
            throw new ConcernException("Concern does not exist.");
        }

        LOG.info("{}: Requested transport list for concern {}", authenticatedUserProvider.getAuthenticatedUser(), concern);

        pdfService.generateTransport(concern, fullDate, response, locale);
    }

    @RequestMapping(value = "patients", produces = "application/pdf", method = RequestMethod.GET)
    public void patients(
            @RequestParam(value = "id") final int concernId,
            final HttpServletResponse response,
            final Locale locale) throws ConcernException {
        Concern concern = concernService.getById(concernId);
        if (concern == null) {
            throw new ConcernException("Concern does not exist.");
        }

        LOG.info("{}: Requested patient list for concern {}", authenticatedUserProvider.getAuthenticatedUser(), concern);

        pdfService.generatePatients(concern, response, locale);
    }

}
