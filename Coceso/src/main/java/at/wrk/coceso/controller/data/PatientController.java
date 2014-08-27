package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/data/patient")
public class PatientController implements IEntityController<Patient> {

  @Autowired
  PatientService patientService;

  @Autowired
  IncidentService incidentService;

  @Override
  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<Patient> getAll(@CookieValue("concern") int concern_id) {
    return patientService.getAll(concern_id);
  }

  @Override
  @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public Patient getById(@PathVariable("id") int id) {
    return patientService.getById(id);
  }

  @Override
  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public String update(@RequestBody Patient patient, BindingResult result,
          @CookieValue("concern") int concern_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    if (result.hasErrors()) {
      return "{\"success\": false, description: \"Binding Error\"}";
    }

    Incident incident = incidentService.getById(patient.getId());
    if (incident == null) {
      return "{\"success\": false, \"info\":\"Invalid ID\"}";
    }
    if (incident.getConcern() != concern_id) {
      return "{\"success\": false, \"info\":\"Active Concern not valid\"}";
    }

    if (patientService.getById(patient.getId()) == null) {

      int ret = patientService.add(patient, user, concern_id);

      return "{\"success\": " + (ret != -1) + ", \"new\": true}";
    }

    return "{\"success\": " + patientService.update(patient, user, concern_id) + ", \"new\": false}";
  }
}
