package at.wrk.coceso.controller.pdf;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.exceptions.ConcernException;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @RequestMapping(value = "report", produces = "application/pdf", method = RequestMethod.GET)
    public void report(
            @RequestParam(value = "id") final int id,
            @RequestParam(value = "fullDate", defaultValue = "0") final boolean fullDate,
            final HttpServletResponse response,
            @AuthenticationPrincipal final User user,
            final Locale locale) throws ConcernException {
        Concern concern = concernService.getById(id);

        if (concern == null) {
            LOG.info("{}: Failed read concern during PDF creation.", user);
            throw new ConcernException();
        }

        LOG.info("{}: Requested final report for concern {}", user, concern);

        pdfService.generateReport(concern, fullDate, response, locale, user);
    }

    @RequestMapping(value = "dump", produces = "application/pdf", method = RequestMethod.GET)
    public void dump(@RequestParam(value = "id") int id, @RequestParam(value = "fullDate", defaultValue = "0") boolean fullDate,
                     HttpServletResponse response, @AuthenticationPrincipal User user, Locale locale) throws ConcernException {
        Concern concern = concernService.getById(id);
        if (Concern.isClosed(concern)) {
            throw new ConcernException();
        }

        LOG.info("{}: Requested pdf dump for concern {}", user, concern);

        pdfService.generateDump(concern, fullDate, response, locale, user);
    }

    @RequestMapping(value = "transport", produces = "application/pdf", method = RequestMethod.GET)
    public void transport(@RequestParam(value = "id") int id, @RequestParam(value = "fullDate", defaultValue = "0") boolean fullDate,
                          HttpServletResponse response, @AuthenticationPrincipal User user, Locale locale) throws ConcernException {
        Concern concern = concernService.getById(id);
        if (concern == null) {
            throw new ConcernException();
        }

        LOG.info("{}: Requested transport list for concern {}", user, concern);

        pdfService.generateTransport(concern, fullDate, response, locale, user);
    }

    @RequestMapping(value = "patients", produces = "application/pdf", method = RequestMethod.GET)
    public void patients(@RequestParam(value = "id") int id,
                         HttpServletResponse response,
                         @AuthenticationPrincipal User user,
                         Locale locale) throws ConcernException {
        Concern concern = concernService.getById(id);
        if (concern == null) {
            throw new ConcernException();
        }

        LOG.info("{}: Requested patient list for concern {}", user, concern);

        pdfService.generatePatients(concern, response, locale, user);
    }

}
