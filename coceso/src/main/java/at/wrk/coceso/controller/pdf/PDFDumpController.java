package at.wrk.coceso.controller.pdf;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.pdf.PDFDumpService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping("/pdfdump")
@Deprecated
public class PDFDumpController {

    private final static
    Logger LOG = Logger.getLogger(PDFDumpController.class);

    @Autowired
    ConcernService concernService;

    @Autowired
    PDFDumpService pdfDumpService;

    private static String dateFormat = "HH:mm:ss";

    @RequestMapping(value = "dump.pdf", produces = "application/pdf", method = RequestMethod.GET)
    public void print(HttpServletResponse response,
                      @RequestParam(value = "id") int id,
                      @RequestParam(value = "fullDate", required = false) Boolean fullDate,
                      Principal principal,
                      Locale locale)
            throws ConcernClosedException, ConcernNotFoundException
    {

        if(fullDate != null && fullDate) {
            dateFormat = "dd.MM.yy HH:mm:ss";
        }

        Operator user = (Operator) ((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        Concern concern = concernService.getById(id);
        if(concern == null) {
            throw new ConcernNotFoundException();
        }

        // If Concern is already closed, Dump is useless
        if(concern.isClosed()) {
            throw new ConcernClosedException();
        }

        LOG.info(String.format("User %s requested PDF Dump for Concern #%d (%s)",
                user.getUsername(), concern.getId(), concern.getName()));


        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            pdfDumpService.init(dateFormat, locale, concern, user);

            pdfDumpService.create(document);

        }
        catch(IOException | DocumentException e) {
            LOG.error("PDFDumpController.print(): " + e.getMessage());
        } finally {
            document.close();
            pdfDumpService.setDestructed();
        }

    }

    @RequestMapping(value = "transportlist.pdf", produces = "application/pdf", method = RequestMethod.GET)
    public void transportlist(HttpServletResponse response,
                      @RequestParam(value = "id") int id,
                      @RequestParam(value = "fullDate", required = false) Boolean fullDate,
                      Principal principal,
                      Locale locale)
            throws ConcernClosedException, ConcernNotFoundException
    {

        if(fullDate != null && fullDate) {
            dateFormat = "dd.MM.yy HH:mm:ss";
        }

        Operator user = (Operator) ((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        Concern concern = concernService.getById(id);
        if(concern == null) {
            throw new ConcernNotFoundException();
        }

        LOG.info(String.format("User %s requested Transport List for Concern #%d (%s)",
                user.getUsername(), concern.getId(), concern.getName()));


        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();


            pdfDumpService.init(dateFormat, locale, concern, user);

            pdfDumpService.createTransportList(document);

        }
        catch(IOException | DocumentException e) {
            LOG.error("PDFDumpController.transportlist(): " + e.getMessage());
        } finally {
            document.close();
            pdfDumpService.setDestructed();
        }

    }

    @ExceptionHandler(ConcernNotFoundException.class)
    public String notFoundError() {
        return "redirect:/home?error=1";
    }

    @ExceptionHandler(ConcernClosedException.class)
    public String closedError() {
        return "redirect:/home?error=1";
    }

    private class ConcernNotFoundException extends Exception {
    }

    private class ConcernClosedException extends Exception {
    }
}
