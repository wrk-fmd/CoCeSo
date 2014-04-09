package at.wrk.coceso.utils;


import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.enums.IncidentType;
import com.itextpdf.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

public class PdfStyle {

    public static final Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD);
    public static final Font subTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18);

    public static final Font title2Font = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    public static final Font descrFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);

    public static final Font defFont = new Font(Font.FontFamily.TIMES_ROMAN, 11);


    private PdfStyle() {
    }

    public static String humanreadableIncidentType(MessageSource messageSource, Locale locale, Incident inc) {
        String type;
        if(inc.getType() == IncidentType.Task) {
            if(inc.getBlue() == null || !inc.getBlue()) {
                type = messageSource.getMessage("label.incident.type.task", null, locale);
            } else {
                type = messageSource.getMessage("label.incident.type.task.blue", null, locale);
            }

        } else {
            type = messageSource.getMessage("label.incident.type." + inc.getType().name().toLowerCase(), null, locale);
        }
        return type;
    }
}
