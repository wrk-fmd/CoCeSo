package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.utils.CocesoLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/data/patient")
public class PatientController implements IEntityController<Patient> {
    @Autowired
    private PatientService patientService;

    @Autowired
    private IncidentService incidentService;

    @Override
    @RequestMapping(value = "getAll", produces = "application/json")
    @ResponseBody
    public List<Patient> getAll(@CookieValue(value = "active_case", defaultValue = "0") String caseId) {
        try {
            return patientService.getAll(Integer.parseInt(caseId));
        } catch(NumberFormatException e) {
            return null;
        }
    }

    @Override
    @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Patient getByPost(@RequestParam(value = "id", required = true) int id) {
        return patientService.getById(id);
    }

    @Override
    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Patient getByGet(int id) {
        return getByPost(id);
    }

    @Override
    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestBody Patient patient, BindingResult result,
                         @CookieValue(value = "active_case", defaultValue = "0") String case_id, Principal principal)
    {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int caseId;

        try {
            caseId = Integer.parseInt(case_id);
        } catch(NumberFormatException e) {
            CocesoLogger.warning("PatientController: update: " + e);
            return "{\"success\": false, \"info\":\"No active Concern. Cookies enabled?\"}";
        }


        if(result.hasErrors()) {
            return "{\"success\": false, description: \"Binding Error\"}";
        }


        Incident incident = incidentService.getById(patient.getId());
        if(incident == null)
            return "{\"success\": false, \"info\":\"Invalid ID\"}";
        if(incident.getConcern() != caseId)
            return "{\"success\": false, \"info\":\"Active Concern not valid\"}";

        if(patientService.getById(patient.getId()) == null) {

            int ret = patientService.add(patient, user, caseId);

            return "{\"success\": " + (ret != -1) + ", \"new\": true}";
        }

        return "{\"success\": " + patientService.update(patient, user, caseId) + ", \"new\": false}";
    }
}
