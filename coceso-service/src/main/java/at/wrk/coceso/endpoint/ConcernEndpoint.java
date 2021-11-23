package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.concern.ConcernBriefDto;
import at.wrk.coceso.dto.concern.ConcernCreateDto;
import at.wrk.coceso.dto.concern.ConcernDto;
import at.wrk.coceso.dto.concern.ConcernUpdateDto;
import at.wrk.coceso.dto.concern.SectionCreateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.ConcernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/concerns")
public class ConcernEndpoint {

    private final ConcernService concernService;

    @Autowired
    public ConcernEndpoint(final ConcernService concernService) {
        this.concernService = concernService;
    }

    @PreAuthorize("hasPermission(null, T(at.wrk.coceso.auth.AccessLevel).CONCERN_READ)")
    @GetMapping
    public List<ConcernBriefDto> getAllConcerns() {
        return concernService.getAllBrief();
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONCERN_READ)")
    @GetMapping("/{concern}")
    public ConcernDto getConcern(@PathVariable final Concern concern) {
        ParamValidator.exists(concern);
        return concernService.getConcern(concern);
    }

    @PreAuthorize("hasPermission(null, T(at.wrk.coceso.auth.AccessLevel).CONCERN_EDIT)")
    @PostMapping
    public ConcernBriefDto createConcern(@RequestBody @Valid final ConcernCreateDto data) {
        return concernService.create(data);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONCERN_EDIT)")
    @PutMapping("/{concern}")
    public void updateConcern(@PathVariable final Concern concern, @RequestBody @Valid final ConcernUpdateDto data) {
        ParamValidator.open(concern);
        concernService.update(concern, data);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONCERN_CLOSE)")
    @PutMapping("/{concern}/close")
    public void closeConcern(@PathVariable final Concern concern) {
        ParamValidator.exists(concern);
        concernService.setClosed(concern, true);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONCERN_CLOSE)")
    @PutMapping("/{concern}/open")
    public void openConcern(@PathVariable final Concern concern) {
        ParamValidator.exists(concern);
        concernService.setClosed(concern, false);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONCERN_EDIT)")
    @PostMapping("/{concern}/sections")
    public void addSection(@PathVariable final Concern concern, @RequestBody @Valid final SectionCreateDto data) {
        ParamValidator.open(concern);
        concernService.addSection(concern, data);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).CONCERN_EDIT)")
    @DeleteMapping("/{concern}/sections/{section}")
    public void removeSection(@PathVariable final Concern concern, @PathVariable final String section) {
        ParamValidator.open(concern);
        concernService.removeSection(concern, section);
    }
}
