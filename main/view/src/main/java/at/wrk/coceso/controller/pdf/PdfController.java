package at.wrk.coceso.controller.pdf;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.exceptions.ConcernException;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.PdfService;
import at.wrk.coceso.utils.PdfDocument;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
  public void report(@RequestParam(value = "id") int id, @RequestParam(value = "fullDate", defaultValue = "0") boolean fullDate,
          HttpServletResponse response, @AuthenticationPrincipal User user, Locale locale) throws ConcernException {
    Concern concern = concernService.getById(id);
    if (concern == null) {
      throw new ConcernException();
    }

    LOG.info("{}: Requested final report for concern {}", user, concern);

    List<Incident> incidents = pdfService.getIncidents(concern);
    PdfDocument document = null;
    try {
      document = pdfService.getDocument(PageSize.A4.rotate(), fullDate, response, locale);
      document.addFrontPage("pdf.report.header", concern, user);
      document.addStatistics(incidents);
      document.addCustomLog(concern);
      document.addUnitsLog(concern);
      document.addIncidentsLog(incidents);
      document.addLastPage();

      LOG.info("{}: Final report for concern {} completely written", user, concern);
    } catch (IOException | DocumentException e) {
      LOG.error("{}: Error on printing final report for concern {}", user, concern, e);
    } finally {
      if (document != null) {
        document.close();
      }
    }
  }

  @RequestMapping(value = "dump", produces = "application/pdf", method = RequestMethod.GET)
  public void dump(@RequestParam(value = "id") int id, @RequestParam(value = "fullDate", defaultValue = "0") boolean fullDate,
          HttpServletResponse response, @AuthenticationPrincipal User user, Locale locale) throws ConcernException {
    Concern concern = concernService.getById(id);
    if (Concern.isClosed(concern)) {
      throw new ConcernException();
    }

    LOG.info("{}: Requested pdf dump for concern {}", user, concern);

    PdfDocument document = null;
    try {
      document = pdfService.getDocument(PageSize.A4.rotate(), fullDate, response, locale);
      document.addFrontPage("pdf.dump.header", concern, user);
      document.addUnitsCurrent(concern);
      document.addIncidentsCurrent(concern);
      document.addLastPage();

      LOG.info("{}: PDF dump for concern {} completely written", user, concern);
    } catch (IOException | DocumentException e) {
      LOG.error("{}: Error on printing pdf dump for concern {}", user, concern, e);
    } finally {
      if (document != null) {
        document.close();
      }
    }
  }

  @RequestMapping(value = "transport", produces = "application/pdf", method = RequestMethod.GET)
  public void transport(@RequestParam(value = "id") int id, @RequestParam(value = "fullDate", defaultValue = "0") boolean fullDate,
          HttpServletResponse response, @AuthenticationPrincipal User user, Locale locale) throws ConcernException {
    Concern concern = concernService.getById(id);
    if (concern == null) {
      throw new ConcernException();
    }

    LOG.info("{}: Requested transport list for concern {}", user, concern);

    PdfDocument document = null;
    try {
      document = pdfService.getDocument(PageSize.A4, fullDate, response, locale);
      document.addFrontPage("pdf.transport.header", concern, user);
      document.addTransports(concern);
      document.addLastPage();

      LOG.info("{}: Transport list for concern {} completely written", user, concern);
    } catch (IOException | DocumentException e) {
      LOG.error("{}: Error on printing transport list for concern {}", user, concern, e);
    } finally {
      if (document != null) {
        document.close();
      }
    }
  }

}
