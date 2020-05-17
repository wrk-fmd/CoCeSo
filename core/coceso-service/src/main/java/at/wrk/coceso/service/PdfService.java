package at.wrk.coceso.service;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.JournalEntry;
import at.wrk.coceso.entity.Task;
import at.wrk.coceso.entity.Unit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public interface PdfService {

    void generateReport(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale);

    void generateDump(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale);

    void generateTransport(Concern concern, boolean fullDate, HttpServletResponse response, Locale locale);

    void generatePatients(Concern concern, HttpServletResponse response, Locale locale);

    List<JournalEntry> getLogByIncident(Incident incident);

    List<JournalEntry> getLogByUnit(Unit unit);

    List<Task> getRelatedUnits(Incident incident);

    Instant getLastUpdate(Incident incident, Unit unit);
}
