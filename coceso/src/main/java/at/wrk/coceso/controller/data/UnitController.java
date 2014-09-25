package at.wrk.coceso.controller.data;

import at.wrk.coceso.dao.CrewDao;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.entity.helper.BatchUnits;
import at.wrk.coceso.entity.helper.UnitWithLocked;
import at.wrk.coceso.service.PersonService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.service.UnitService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/data/unit")
public class UnitController implements IEntityController<Unit> {

    private static final Logger logger = Logger.getLogger(UnitController.class);

    @Autowired
    private UnitService unitService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PersonService personService;

    @Autowired
    private CrewDao crewDao;

    @Override
    @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public List<Unit> getAll(@CookieValue("concern") int concern_id) {
        return unitService.getAll(concern_id);
    }

    @RequestMapping(value = "getAllWithLocked", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public List<UnitWithLocked> getAllWithLocked(@CookieValue("concern") int concern_id) {
        return unitService.getAllWithLocked(concern_id);
    }

    @Override
    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Unit getById(@PathVariable(value = "id") int id) {
        return unitService.getById(id);
    }

    // TODO Error Handling
    @RequestMapping(value = "createUnitBatch", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String createUnitBatch(@RequestBody BatchUnits batch, BindingResult result,
                                  @CookieValue("concern") int concern_id, UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();

        Unit unit = new Unit();
        unit.setConcern(concern_id);
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

    @Override
    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestBody Unit unit, BindingResult result,
                         @CookieValue("concern") int concern_id, UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();

        if (result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }

        if (unit.getId() <= 0) {
            //Adding units is not possible in "main" page, therefore not necessary here
            logger.warn("UnitController.update(): User " + user.getUsername() + " tried to add unit, use updateFull!");
            return "{\"success\": false, \"info\":\"Adding not allowed\"}";
        }

        Unit u = unitService.getById(unit.getId());
        if (u.getConcern() != concern_id) {
            logger.warn("UnitController.update(): User " + user.getUsername() + " tried to update Unit of wrong Concern");
            return "{\"success\": false, \"info\":\"Active Concern not valid\"}";
        }
        unit.setConcern(concern_id);

        boolean ret = unitService.update(unit, user);
        String associated = "{}";
        if (ret && unit.getIncidents() != null) {
            associated = setAssociated(unit, user);
        }

        return "{\"success\": " + ret + ", \"new\": false,\"associated\":" + associated + "}";
    }

    /**
     * Sets TaskStates of all assigned incidents.
     * @param unit Unit, frontend provided, with Map "incidents" for TaskStates
     * @param user User, who triggered the update
     * @return JSON Object, id and return value (boolean) of TaskState update
     */
    protected String setAssociated(Unit unit, Operator user) {
        if(unit == null) {
            logger.warn("tried to call with unit=null");
            return "{}";
        }

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

    @ResponseBody
    @RequestMapping(value = "updateFull", produces = "application/json", method = RequestMethod.POST)
    public String updateFull(@RequestBody Unit editedUnit, BindingResult result,
                             @CookieValue("concern") int concern_id, UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();

        if (result.hasErrors()) {
            return "{\"success\":false,description:\"Binding Error\"}";
        }

        // Create new Unit
        if (editedUnit.getId() <= 0) {
            editedUnit.setId(0);
            editedUnit.setConcern(concern_id);

            editedUnit.setId(unitService.add(editedUnit, user));

            boolean success = (editedUnit.getId() > 0);
            if (!success) {
                logger.warn("UnitController.updateFull(): User " + user.getUsername() + ": Creating unit with call=" + editedUnit.getCall() + " failed. returned id=" + editedUnit.getId());
            }
            return "{\"success\": " + success + ", \"new\": true, \"unit_id\":" + editedUnit.getId() + "}";
        }

        Unit unit = unitService.getById(editedUnit.getId());
        if (unit.getConcern() != concern_id) {
            logger.warn("UnitController.updateFull(): User " + user.getUsername() + " tried to update Unit of wrong Concern");
            return "{\"success\": false, \"info\":\"Active Concern not valid\"}";
        }

        unit.setCall(editedUnit.getCall());
        unit.setAni(editedUnit.getAni());
        unit.setInfo(editedUnit.getInfo());
        unit.setPortable(editedUnit.isPortable());
        unit.setWithDoc(editedUnit.isWithDoc());
        unit.setTransportVehicle(editedUnit.isTransportVehicle());
        unit.setHome(editedUnit.getHome());

        boolean ret = unitService.updateFull(unit, user);

        return "{\"success\":" + ret + "}";
    }

    @RequestMapping(value = "sendHome", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String sendHome(@CookieValue("concern") int concern_id, @RequestParam("id") int unit_id,
                           UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();

        if (!unitService.sendHome(concern_id, unit_id, user)) {
            return "{\"success\":false}";
        }
        return "{\"success\":true}";
    }

    @RequestMapping(value = "holdPosition", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String holdPosition(@CookieValue("concern") int concern_id, @RequestParam("id") int unit_id,
                               UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();

        if (!unitService.holdPosition(concern_id, unit_id, user)) {
            return "{\"success\":false}";
        }
        return "{\"success\":true}";
    }

    @RequestMapping(value = "standby", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String standby(@CookieValue("concern") int concern_id, @RequestParam("id") int unit_id,
                          UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();

        if (!unitService.standby(concern_id, unit_id, user)) {
            return "{\"success\":false}";
        }
        return "{\"success\":true}";
    }

    @RequestMapping(value = "remove", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String remove(@RequestParam("id") int unit_id, UsernamePasswordAuthenticationToken token) {
        Operator user = (Operator) token.getPrincipal();

        return "{\"success\":" + unitService.remove(unit_id, user) + "}";
    }

    @RequestMapping(value = "assignPerson", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String assignPerson(@RequestParam("unit_id") int unit_id, @RequestParam("person_id") int person_id) {
        if (!crewDao.add(unitService.getById(unit_id), personService.getById(person_id))) {
            return "{\"success\":false}";
        }
        return "{\"success\":true}";
    }

    @RequestMapping(value = "removePerson", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String removePerson(@RequestParam("unit_id") int unit_id, @RequestParam("person_id") int person_id) {
        if (!crewDao.remove(unitService.getById(unit_id), personService.getById(person_id))) {
            return "{\"success\":false}";
        }
        return "{\"success\":true}";
    }

}
