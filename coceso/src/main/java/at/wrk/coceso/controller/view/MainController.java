package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.UnitService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/main")
public class MainController {

  private final static Logger LOG = Logger.getLogger(MainController.class);

  @Autowired
  private ConcernService concernService;

  @Autowired
  private UnitService unitService;

  @Autowired
  private IncidentService incidentService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String showMain(ModelMap map, @CookieValue(value = "concern", required = false) Integer concern_id,
          UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();
    String error_return = "redirect:/home?error=1";

    if (concern_id == null) {
      LOG.info("invalid concern_id");
      return error_return;
    }

    Concern concern = concernService.getById(concern_id);
    if (concern == null || concern.isClosed()) {
      LOG.info("concern non existent or closed");
      return error_return;
    }

    LOG.debug(String.format("User %s started CoCeSo, Main Program. Concern #%d (%s)",
            user == null ? "N/A" : user.getUsername(), concern.getId(), concern.getName()));

    map.addAttribute("concern", concern);

    return "main";
  }

  @RequestMapping(value = "unit", method = RequestMethod.GET)
  public String unit() {
    return "main_content/unit";
  }

  @RequestMapping(value = "unit_hierarchy", method = RequestMethod.GET)
  public String unitHierarchy() {
    return "main_content/unit_hierarchy";
  }

  @RequestMapping(value = "unit_form", method = RequestMethod.GET)
  public String unitForm() {
    return "main_content/unit_form";
  }

  @RequestMapping(value = "unit_detail", method = RequestMethod.GET)
  public String unitDetail() {
    return "main_content/unit_detail";
  }

  @RequestMapping(value = "incident", method = RequestMethod.GET)
  public String incident() {
    return "main_content/incident";
  }

  @RequestMapping(value = "incident_form", method = RequestMethod.GET)
  public String incidentForm() {
    return "main_content/incident_form";
  }

  @RequestMapping(value = "log", method = RequestMethod.GET)
  public String log() {
    return "main_content/log";
  }

  @RequestMapping(value = "key", method = RequestMethod.GET)
  public String key() {
    return "main_content/key";
  }

  @RequestMapping(value = "patient_form", method = RequestMethod.GET)
  public String patientForm() {
    return "main_content/patient_form";
  }

  @RequestMapping(value = "log_add", method = RequestMethod.GET)
  public String logAdd() {
    return "main_content/log_add";
  }

  @RequestMapping(value = "dump", method = RequestMethod.GET)
  public String dump(ModelMap map, @CookieValue("concern") int concern_id) {
    Concern c = concernService.getById(concern_id);
    if (c == null || c.isClosed()) {
      return "redirect:/home?error=1";
    }

    map.addAttribute("concern", c);
    map.addAttribute("units", unitService.getAll(concern_id));
    map.addAttribute("incidents", incidentService.getAllActive(concern_id));
    map.addAttribute("date", new Date());

    return "main_content/dump";
  }
}
