package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.staff.StaffMemberCreateDto;
import at.wrk.coceso.dto.staff.StaffMemberDto;
import at.wrk.coceso.dto.staff.StaffMemberUpdateDto;
import at.wrk.coceso.entity.StaffMember;
import at.wrk.coceso.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/staff")
public class StaffEndpoint {

    private final StaffService staffService;

    @Autowired
    public StaffEndpoint(final StaffService staffService) {
        this.staffService = staffService;
    }

    @PreAuthorize("hasPermission(null, T(at.wrk.coceso.auth.AccessLevel).STAFF_READ)")
    @GetMapping
    public Page<StaffMemberDto> getAllStaff(final Pageable pageable, @RequestParam(required = false) final String filter) {
        return staffService.getAll(pageable, filter);
    }

    @PreAuthorize("hasPermission(null, T(at.wrk.coceso.auth.AccessLevel).STAFF_EDIT)")
    @PostMapping
    public StaffMemberDto createStaffMember(@RequestBody @Valid final StaffMemberCreateDto data) {
        return staffService.create(data);
    }

    @PreAuthorize("hasPermission(#staffMember, T(at.wrk.coceso.auth.AccessLevel).STAFF_EDIT)")
    @PutMapping("/{staffMember}")
    public void updateStaffMember(@PathVariable final StaffMember staffMember, @RequestBody @Valid final StaffMemberUpdateDto data) {
        ParamValidator.exists(staffMember);
        staffService.update(staffMember, data);
    }

    @PreAuthorize("hasPermission(#staffMember, T(at.wrk.coceso.auth.AccessLevel).UNIT_EDIT)")
    @DeleteMapping("/{staffMember}")
    public void removeStaffMember(@PathVariable final StaffMember staffMember) {
        ParamValidator.exists(staffMember);
        staffService.remove(staffMember);
    }

    @PreAuthorize("hasPermission(null, T(at.wrk.coceso.auth.AccessLevel).STAFF_EDIT)")
    @PostMapping(consumes = "text/csv")
    public Collection<StaffMemberDto> upload(@RequestBody String body) {
        return staffService.importCsv(body);
    }
}
