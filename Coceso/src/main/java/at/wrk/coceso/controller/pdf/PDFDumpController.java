package at.wrk.coceso.controller.pdf;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.pdf.PDFDumpService;
import at.wrk.coceso.utils.Logger;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

/**
 * Created by Robert on 08.04.2014.
 */
@Controller
@RequestMapping("/pdfdump")
public class PDFDumpController {

    @Autowired
    ConcernService concernService;

    @Autowired
    PDFDumpService pdfDumpService;

    private static String dateFormat = "HH:mm:ss";

    @RequestMapping(value = "dump.pdf", produces = "application/pdf")
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

        Logger.info("User " + user.getUsername() + " requested PDF Dump for Concern #" + id + " (" + concern.getName() + ")");


        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();


            pdfDumpService.init(dateFormat, locale, concern, user);

            pdfDumpService.create(document);

        }
        catch(IOException e) {
            Logger.error("PDFDumpController.print(): " + e.getMessage());
        }
        catch(DocumentException e) {
            Logger.error("PDFDumpController.print(): " + e.getMessage());
        }
        finally {
            document.close();
        }

    }


    @ExceptionHandler(ConcernNotFoundException.class)
    public String error() {
        return "redirect:/welcome?error=1";
    }

    @ExceptionHandler(ConcernClosedException.class)
    public String error2() {
        return "redirect:/welcome?error=4";
    }


    private class ConcernNotFoundException extends Exception {
    }

    private class ConcernClosedException extends Exception {
    }
}
