package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entity.helper.UnitWithLocked;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.utils.CocesoLogger;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/data/unit")
public class UnitController {

  @Autowired
  private UnitService unitService;

  @Autowired
  private TaskService taskService;

  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<Unit> getAll(@CookieValue(value = "active_case", defaultValue = "0") int concernId) {
    return unitService.getAll(concernId);
  }

  @RequestMapping(value = "getAllWithLocked", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<UnitWithLocked> getAllWithLocked(@CookieValue(value = "active_case", defaultValue = "0") int concernId) {
    return unitService.getAllWithLocked(concernId);
  }

  @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public Unit getById(@PathVariable(value = "id") int id) {
    return unitService.getById(id);
  }

  // TODO Error Handling
  @RequestMapping(value = "createUnitBatch", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public String createUnitBatch(@RequestBody BatchUnits batch, BindingResult result,
          @CookieValue(value = "active_case", defaultValue = "0") int concernId, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    Unit unit = new Unit();
    unit.setConcern(concernId);
    unit.setId(-1);
    unit.setPortable(batch.isPortable());
    unit.setWithDoc(batch.isWithDoc());
    unit.setTransportVehicle(batch.isTransportVehicle());
    unit.setHome(batch.getHome());

    for (int i = batch.getFrom(); i <= batch.getTo(); i++) {
      unit.setCall(batch.getCall() + i);
      unitService.add(unit, user);
    }

    return "{\"success\":true}";
  }

  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> update(@RequestBody Unit unit, BindingResult result,
          @CookieValue(value = "active_case", defaultValue = "0") int concernId, Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    if (result.hasErrors()) {
      return new ResponseEntity<String>("{\"success\": false, description: \"Binding Error\"}", HttpStatus.BAD_REQUEST);
    }

    if (unit.getId() <= 0) {
      //Adding units is not possible in "main" page, therefore not necessary here
      CocesoLogger.warning("UnitController.update(): User " + user.getUsername() + " tried to add unit, use updateFull!");
      return new ResponseEntity<String>("{\"success\": false, \"info\":\"Adding not allowed\"}", HttpStatus.BAD_REQUEST);
    }

    Unit u = unitService.getById(unit.getId());
    if (u.getConcern() != concernId) {
      CocesoLogger.warning("UnitController.update(): User " + user.getUsername() + " tried to update Unit of wrong Concern");
      return new ResponseEntity<String>("{\"success\": false, \"info\":\"Active Concern not valid\"}", HttpStatus.BAD_REQUEST);
    }

    //TODO: Is this still necessary? Every unit already in DB has to have a concern anyhow...
    if (unit.getConcern() <= 0) {
      // Something went wrong with active Concern
      CocesoLogger.info("UnitController.update(): User " + user.getUsername() + ":  Invalid Concern ID");
      return new ResponseEntity<String>("{\"success\": false, \"info\":\"No active Concern. Cookies enabled?\"}", HttpStatus.BAD_REQUEST);
    }

    boolean ret = unitService.update(unit, user);
    String associated = "{}";
    if (ret && unit.getIncidents() != null) {
      associated = setAssociated(unit, user);
    }

    //log.logFull(user, "Unit updated", caseId, unit, null, true);
    return new ResponseEntity<String>("{\"success\": " + ret + ", \"new\": false,\"associated\":" + associated + "}",
            ret ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
  }

  protected String setAssociated(Unit unit, Operator user) {
    String messages = "{";
    for (Map.Entry<Integer, TaskState> entry : unit.getIncidents().entrySet()) {
      if (messages.length() > 1) {
        messages += ",";
      }
      messages += "\"" + entry.getKey() + "\":" + taskService.changeState(entry.getKey(), unit.getId(), entry.getValue(), user);
    }
    messages += "}";
    return messages;
  }

  @RequestMapping(value = "updateFull", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> updateFull(@RequestBody Unit editedUnit, BindingResult result,
          @CookieValue(value = "active_case", defaultValue = "0") int concernId, Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    if (result.hasErrors()) {
      return new ResponseEntity<String>("{\"success\": false, description: \"Binding Error\"}", HttpStatus.BAD_REQUEST);
    }

    // Create new Unit
    if (editedUnit.getId() <= 0) {
      editedUnit.setId(0);
      editedUnit.setConcern(concernId);

      editedUnit.setId(unitService.add(editedUnit, user));

      boolean success = (editedUnit.getId() > 0);
      if (!success) {
        CocesoLogger.warning("UnitController.update(): User " + user.getUsername() + ":  Creating unit with call="
                + editedUnit.getCall() + " failed. returned id=" + editedUnit.getId());
      }
      return new ResponseEntity<String>("{\"success\": " + success + ", \"new\": true, \"unit_id\":" + editedUnit.getId() + "}", (success ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
    }

    Unit unit = unitService.getById(editedUnit.getId());
    if (unit.getConcern() != concernId) {
      CocesoLogger.warning("UnitController.update(): User " + user.getUsername() + " tried to update Unit of wrong Concern");
      return new ResponseEntity<String>("{\"success\": false, \"info\":\"Active Concern not valid\"}", HttpStatus.BAD_REQUEST);
    }

    unit.setCall(editedUnit.getCall());
    unit.setAni(editedUnit.getAni());
    unit.setInfo(editedUnit.getInfo());
    unit.setPortable(editedUnit.isPortable());
    unit.setWithDoc(editedUnit.isWithDoc());
    unit.setTransportVehicle(editedUnit.isTransportVehicle());
    unit.setHome(editedUnit.getHome());

    boolean ret = unitService.updateFull(unit, user);

    return new ResponseEntity<String>("{\"success\":" + ret + "}", ret ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
  }

  @RequestMapping(value = "sendHome", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> sendHome(@CookieValue(value = "active_case", defaultValue = "0") int concernId,
          @RequestParam("id") int unitId, Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    if (!unitService.sendHome(concernId, unitId, user)) {
      return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
  }

  @RequestMapping(value = "holdPosition", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> holdPosition(@CookieValue(value = "active_case", defaultValue = "0") int concernId,
          @RequestParam("id") int unitId, Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    if (!unitService.holdPosition(concernId, unitId, user)) {
      return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
  }

  @RequestMapping(value = "standby", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> standby(@CookieValue(value = "active_case", defaultValue = "0") int concernId,
          @RequestParam("id") int unitId, Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    if (!unitService.standby(concernId, unitId, user)) {
      return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
  }

  @RequestMapping(value = "remove", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> remove(@RequestParam("id") int unitId, Principal principal) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
    Operator user = (Operator) token.getPrincipal();

    return new ResponseEntity<String>("{\"success\":" + unitService.remove(unitId, user) + "}", HttpStatus.OK);
  }

}
