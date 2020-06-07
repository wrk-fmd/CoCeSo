package at.wrk.coceso.endpoint;

import at.wrk.coceso.dto.message.SendMessageDto;
import at.wrk.coceso.dto.unit.UnitBatchCreateDto;
import at.wrk.coceso.dto.unit.UnitBriefDto;
import at.wrk.coceso.dto.unit.UnitCreateDto;
import at.wrk.coceso.dto.unit.UnitDto;
import at.wrk.coceso.dto.unit.UnitUpdateDto;
import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.StaffMember;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.MessageService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.service.UnitService;
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
import java.util.Collection;

@RestController
@RequestMapping("/concerns/{concern}/units")
public class UnitEndpoint {

    private final UnitService unitService;
    private final TaskService taskService;
    private final MessageService messageService;

    @Autowired
    public UnitEndpoint(final UnitService unitService, final TaskService taskService, final MessageService messageService) {
        this.unitService = unitService;
        this.taskService = taskService;
        this.messageService = messageService;
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).UNIT_READ)")
    @GetMapping
    public Collection<UnitDto> getAllUnits(@PathVariable final Concern concern) {
        ParamValidator.open(concern);
        return unitService.getAll(concern);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).UNIT_EDIT)")
    @PostMapping
    public UnitBriefDto createUnit(@PathVariable final Concern concern, @RequestBody @Valid final UnitCreateDto data) {
        ParamValidator.open(concern);
        return unitService.create(concern, data);
    }

    @PreAuthorize("hasPermission(#concern, T(at.wrk.coceso.auth.AccessLevel).UNIT_EDIT)")
    @PostMapping("/batch")
    public Collection<UnitBriefDto> createUnitsBatch(@PathVariable final Concern concern,
            @RequestBody @Valid final UnitBatchCreateDto data) {
        ParamValidator.open(concern);
        return unitService.createBatch(concern, data);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).UNIT_EDIT)")
    @PutMapping("/{unit}")
    public void updateUnit(@PathVariable final Concern concern, @PathVariable final Unit unit,
            @RequestBody @Valid final UnitUpdateDto data) {
        ParamValidator.open(concern, unit);
        unitService.update(unit, data);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).UNIT_EDIT)")
    @DeleteMapping("/{unit}")
    public void removeUnit(@PathVariable final Concern concern, @PathVariable final Unit unit) {
        ParamValidator.open(concern, unit);
        unitService.remove(unit);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).UNIT_ASSIGN)")
    @PostMapping("/{unit}/tasks/home")
    public void sendHome(@PathVariable final Concern concern, @PathVariable final Unit unit) {
        ParamValidator.open(concern, unit);
        taskService.sendHome(unit);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).UNIT_ASSIGN)")
    @PostMapping("/{unit}/tasks/holdPosition")
    public void holdPosition(@PathVariable final Concern concern, @PathVariable final Unit unit) {
        ParamValidator.open(concern, unit);
        taskService.holdPosition(unit);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).UNIT_ASSIGN)")
    @PostMapping("/{unit}/tasks/standby")
    public void standby(@PathVariable final Concern concern, @PathVariable final Unit unit) {
        ParamValidator.open(concern, unit);
        taskService.standby(unit);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).UNIT_EDIT)")
    @PutMapping("/{unit}/crew/{member}")
    public void assignCrewMember(@PathVariable final Concern concern, @PathVariable final Unit unit,
            @PathVariable final StaffMember member) {
        ParamValidator.open(concern, unit);
        unitService.addCrewMember(unit, member);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).UNIT_EDIT)")
    @DeleteMapping("/{unit}/crew/{member}")
    public void removeCrewMember(@PathVariable final Concern concern, @PathVariable final Unit unit,
            @PathVariable final StaffMember member) {
        ParamValidator.open(concern, unit);
        unitService.removeCrewMember(unit, member);
    }

    @PreAuthorize("hasPermission(#unit, T(at.wrk.coceso.auth.AccessLevel).UNIT_MESSAGE)")
    @PutMapping("/{unit}/message")
    public void sendMessage(@PathVariable final Concern concern, @PathVariable final Unit unit,
            @RequestBody @Valid final SendMessageDto data) {
        ParamValidator.open(concern, unit);
        messageService.sendMessage(unit, data);
    }
}
