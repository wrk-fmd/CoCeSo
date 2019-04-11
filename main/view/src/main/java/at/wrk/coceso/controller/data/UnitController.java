package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.service.UnitWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data/unit")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitWriteService unitWriteService;

    private final EntityEventHandler<Unit> entityEventHandler;

    @Autowired
    public UnitController(EntityEventFactory entityEventFactory) {
        this.entityEventHandler = entityEventFactory.getEntityEventHandler(Unit.class);
    }

    @PreAuthorize("@auth.hasAccessLevel('Main')")
    @JsonView(JsonViews.Main.class)
    @Transactional
    @RequestMapping(value = "main", produces = "application/json", method = RequestMethod.GET)
    public SequencedResponse<List<Unit>> getForMain(final @ActiveConcern Concern concern) {
        return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()),
                Initializer.init(unitService.getAll(concern), Unit::getIncidents));
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @JsonView(JsonViews.Edit.class)
    @Transactional
    @RequestMapping(value = "edit", produces = "application/json", method = RequestMethod.GET)
    public SequencedResponse<List<Unit>> getForEdit(final @ActiveConcern Concern concern) {
        return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()),
                Initializer.init(unitService.getAll(concern), Unit::isLocked));
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @RequestMapping(value = "createBatch", produces = "application/json", method = RequestMethod.POST)
    public RestResponse createBatch(
            final @RequestBody BatchUnits batch,
            final BindingResult result,
            final @ActiveConcern Concern concern,
            final @AuthenticationPrincipal User user) {
        return new RestResponse(true, new RestProperty("ids", unitWriteService.batchCreate(batch, concern, user)));
    }

    @PreAuthorize("@auth.hasAccessLevel('Main')")
    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    public RestResponse updateMain(
            final @RequestBody Unit unit,
            final BindingResult result,
            final @AuthenticationPrincipal User user) {
        if (result.hasErrors()) {
            return new RestResponse(result);
        }

        unitWriteService.updateMain(unit, user);
        return new RestResponse(true, new RestProperty("new", false));
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @RequestMapping(value = "updateFull", produces = "application/json", method = RequestMethod.POST)
    public RestResponse updateEdit(
            final @RequestBody Unit unit,
            final BindingResult result,
            final @ActiveConcern Concern concern,
            final @AuthenticationPrincipal User user) {
        if (result.hasErrors()) {
            return new RestResponse(result);
        }

        boolean isNew = unit.getId() == null;
        Unit updatedUnit = unitWriteService.updateEdit(unit, concern, user);
        return new RestResponse(true, new RestProperty("new", isNew), new RestProperty("unit_id", updatedUnit.getId()));
    }

    @PreAuthorize("@auth.hasAccessLevel('Main')")
    @RequestMapping(value = "sendHome", produces = "application/json", method = RequestMethod.POST)
    public RestResponse sendHome(@RequestParam("id") int unitId, @AuthenticationPrincipal User user) {
        unitWriteService.sendHome(unitId, user);
        return new RestResponse(true);
    }

    @PreAuthorize("@auth.hasAccessLevel('Main')")
    @RequestMapping(value = "holdPosition", produces = "application/json", method = RequestMethod.POST)
    public RestResponse holdPosition(@RequestParam("id") int unit_id, @AuthenticationPrincipal User user) {
        unitWriteService.holdPosition(unit_id, user);
        return new RestResponse(true);
    }

    @PreAuthorize("@auth.hasAccessLevel('Main')")
    @RequestMapping(value = "standby", produces = "application/json", method = RequestMethod.POST)
    public RestResponse standby(@RequestParam("id") int unit_id, @AuthenticationPrincipal User user) {
        unitWriteService.standby(unit_id, user);
        return new RestResponse(true);
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @RequestMapping(value = "remove", produces = "application/json", method = RequestMethod.POST)
    public RestResponse remove(@RequestParam("id") int unit_id, @AuthenticationPrincipal User user) {
        unitWriteService.remove(unit_id, user);
        return new RestResponse(true);
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @RequestMapping(value = "assignPerson", produces = "application/json", method = RequestMethod.POST)
    public RestResponse assignPerson(@RequestParam("unit_id") int unit_id, @RequestParam("person_id") int person_id) {
        unitWriteService.addCrew(unit_id, person_id);
        return new RestResponse(true);
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @RequestMapping(value = "removePerson", produces = "application/json", method = RequestMethod.POST)
    public RestResponse removePerson(@RequestParam("unit_id") int unit_id, @RequestParam("person_id") int person_id) {
        unitWriteService.removeCrew(unit_id, person_id);
        return new RestResponse(true);
    }

    @PreAuthorize("@auth.hasAccessLevel('Edit')")
    @RequestMapping(value = "upload", produces = "application/json", consumes = "text/csv", method = RequestMethod.POST)
    public RestResponse upload(@RequestBody String body, @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
        return new RestResponse(true, new RestProperty("counter", unitWriteService.importUnits(body, concern, user)));
    }

}
