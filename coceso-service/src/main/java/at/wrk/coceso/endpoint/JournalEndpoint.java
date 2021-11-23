package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.journal.CustomJournalEntryDto;
import at.wrk.coceso.dto.journal.JournalEntryDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/concerns/{concern}/journal")
public class JournalEndpoint {

    private final JournalService journalService;

    @Autowired
    public JournalEndpoint(final JournalService journalService) {
        this.journalService = journalService;
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).JOURNAL_READ)")
    @GetMapping
    public List<JournalEntryDto> getCustomJournalEntries(@PathVariable final Concern concern) {
        ParamValidator.open(concern);
        return journalService.getCustom(concern);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).JOURNAL_READ)")
    @GetMapping("/last/{limit}")
    public List<JournalEntryDto> getLastJournalEntries(@PathVariable final Concern concern, @PathVariable final int limit) {
        ParamValidator.open(concern);
        return journalService.getLast(concern, limit);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).JOURNAL_READ)")
    @GetMapping("/units/{unit}")
    public List<JournalEntryDto> getJournalByUnit(@PathVariable final Concern concern, @PathVariable final Unit unit,
            @RequestParam(required = false) final Integer limit) {
        ParamValidator.open(concern, unit);
        return journalService.getByUnit(unit, limit);
    }

    @PreAuthorize("hasPermission(#incident, T(at.wrk.coceso.auth.AccessLevel).JOURNAL_READ)")
    @GetMapping("/incidents/{incident}")
    public List<JournalEntryDto> getJournalByIncident(@PathVariable final Concern concern, @PathVariable final Incident incident,
            @RequestParam(required = false) final Integer limit) {
        ParamValidator.open(concern, incident);
        return journalService.getByIncident(incident, limit);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).JOURNAL_CREATE)")
    @PostMapping
    public void addCustomJournalEntry(@PathVariable final Concern concern, @RequestBody @Valid final CustomJournalEntryDto data) {
        ParamValidator.open(concern);
        journalService.logCustom(concern, data);
    }
}
