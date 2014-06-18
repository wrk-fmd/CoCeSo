
package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Point;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.UnitService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.utils.CocesoLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/data/unit")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<Unit> getAll(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return unitService.getAll(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            CocesoLogger.warn("UnitController: getAll: "+e);
            return null;
        }
    }

    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Unit getByPost(@RequestParam(value = "id", required = true) int id) {

        return unitService.getById(id);
    }

    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Unit getByGet(@PathVariable("id") int id) {

        return getByPost(id);
    }

    @RequestMapping(value = "getNonDeletables", produces = "application/json")
    @ResponseBody
    public Set<Integer> getNonDeletables(@CookieValue(value = "active_case", defaultValue = "0") String case_id) {

        try {
            return unitService.getNonDeletable(Integer.parseInt(case_id));
        } catch(NumberFormatException e) {
            CocesoLogger.warn("UnitController: getAll: "+e);
            return null;
        }
    }

    /**
     * Batch Job for Creating Units
     *
     * POST data:
     * portable, withDoc, transportVehicle, home, from, to, call_pre
     * @param request The HttpRequest
     * @param case_id Active Concern out of Cookie
     * @param principal Logged-In User
     * @return JSON with Success
     */
    // TODO Error Handling
    @RequestMapping(value = "createUnitBatch", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String createUnitBatch(HttpServletRequest request,
                                  @CookieValue("active_case") int case_id,
                                  Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        Unit unit = new Unit();
        unit.setConcern(case_id);

        unit.setId(-1);
        unit.setPortable(request.getParameter("portable") != null);
        unit.setWithDoc(request.getParameter("withDoc") != null);
        unit.setTransportVehicle(request.getParameter("transportVehicle") != null);
        unit.setHome(new Point(request.getParameter("home")));

        int from = Integer.parseInt(request.getParameter("from"));
        int to = Integer.parseInt(request.getParameter("to"));

        for(int i = from; i <= to; i++){
            unit.setCall(request.getParameter("call_pre")+i);
            unitService.add(unit, user);
        }

        return "{\"success\":true}";
    }


    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> update(@RequestBody Unit unit, BindingResult result,
                         @CookieValue(value = "active_case", defaultValue = "0") String case_id, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int caseId;

        try {
            caseId = Integer.parseInt(case_id);
        } catch(NumberFormatException e) {
            CocesoLogger.warn("UnitController: update: "+e);
            return new ResponseEntity<String>("{\"success\": false, \"info\":\"No active Concern. Cookies enabled?\"}", HttpStatus.BAD_REQUEST);
        }


        if(result.hasErrors()) {
            return new ResponseEntity<String>("{\"success\": false, description: \"Binding Error\"}", HttpStatus.BAD_REQUEST);
        }

        // if Unit already exists, check if in active Concern
        if(unit.getId() > 0) {
            Unit u = unitService.getById(unit.getId());
            if(u.getConcern() != caseId) {
                CocesoLogger.warn("UnitController.update(): User " + user.getUsername() + " tried to update Unit of wrong Concern");
                return new ResponseEntity<String>("{\"success\": false, \"info\":\"Active Concern not valid\"}", HttpStatus.BAD_REQUEST);
            }
        }

        // if exist: already checked, if new: set Concern
        unit.setConcern(caseId);

        if(unit.getConcern() <= 0) {
            // Something went wrong with active Concern
            CocesoLogger.info("UnitController.update(): User " + user.getUsername() + ":  Invalid Concern ID");
            return new ResponseEntity<String>("{\"success\": false, \"info\":\"No active Concern. Cookies enabled?\"}", HttpStatus.BAD_REQUEST);
        }

        // Create new Unit
        if(unit.getId() < 1) {
            unit.setId(0);

            unit.setId(unitService.add(unit, user));

            if(unit.getId() <= 0) {
                CocesoLogger.warn("UnitController.update(): User " + user.getUsername() + ":  Creating unit with call=" +
                                unit.getCall() + " failed. returned id=" + unit.getId());
            }
            boolean success = (unit.getId() > 0);
            return new ResponseEntity<String>("{\"success\": " + success + ", \"new\": true, \"unit_id\":"+ unit.getId() +"}", (success ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
        }

        boolean ret = unitService.update(unit, user);
        String associated = "{}";
        if (ret)
            associated = setAssociated(unit, user);

        //log.logFull(user, "Unit updated", caseId, unit, null, true);
        return new ResponseEntity<String>("{\"success\": " + ret + ", \"new\": false}",
                ret ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    protected String setAssociated(Unit unit, Operator user) {
      String messages = "{";
      for (Map.Entry<Integer,TaskState> entry : unit.getIncidents().entrySet()) {
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
    public ResponseEntity<String> updateFull(@RequestBody Unit editedUnit, BindingResult result, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        if(result.hasErrors()) {
            return new ResponseEntity<String>("{\"success\": false, description: \"Binding Error\"}", HttpStatus.BAD_REQUEST);
        }


        Unit unit = unitService.getById(editedUnit.getId());

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
    public ResponseEntity<String> sendHomeByPost(@CookieValue(value="active_case", defaultValue = "0") String case_id,
                         @RequestParam("id") int unitId, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int activeCase = Integer.parseInt(case_id);

        if(!unitService.sendHome(activeCase, unitId, user)) {
            return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
    }

    @RequestMapping(value = "holdPosition", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> holdPosition(@CookieValue(value="active_case", defaultValue = "0") String case_id,
                                                 @RequestParam("id") int unitId, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int activeCase = Integer.parseInt(case_id);

        if(!unitService.holdPosition(activeCase, unitId, user)) {
            return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
    }

    @RequestMapping(value = "standby", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> standby(@CookieValue(value="active_case", defaultValue = "0") String case_id,
                                               @RequestParam("id") int unitId, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int activeCase = Integer.parseInt(case_id);

        if(!unitService.standby(activeCase, unitId, user)) {
            return new ResponseEntity<String>("{\"success\":false}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("{\"success\":true}", HttpStatus.OK);
    }

    @RequestMapping(value = "remove", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> remove(@RequestBody Unit editedUnit, BindingResult result, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        if(result.hasErrors()) {
            return new ResponseEntity<String>("{\"success\": false, description: \"Binding Error\"}", HttpStatus.BAD_REQUEST);
        }

        boolean ret = unitService.remove(editedUnit, user);

        return new ResponseEntity<String>("{\"success\":" + ret + "}", HttpStatus.OK);
    }


}
