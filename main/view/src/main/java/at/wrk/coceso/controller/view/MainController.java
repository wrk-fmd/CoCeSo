package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.utils.ActiveConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("@auth.hasAccessLevel('Main')")
@RequestMapping("/main")
public class MainController {

  private final static Logger LOG = LoggerFactory.getLogger(MainController.class);

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String showMain(ModelMap map, @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    LOG.debug("{}: Started main program with concern {}", user, concern);
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

  @RequestMapping(value = "patient", method = RequestMethod.GET)
  public String patient() {
    return "main_content/patient";
  }

  @RequestMapping(value = "patient_form", method = RequestMethod.GET)
  public String patientForm() {
    return "main_content/patient_form";
  }

  @RequestMapping(value = "log_add", method = RequestMethod.GET)
  public String logAdd() {
    return "main_content/log_add";
  }

  @RequestMapping(value = "radio", method = RequestMethod.GET)
  public String radio() {
    return "main_content/radio";
  }

  @RequestMapping(value = "map", method = RequestMethod.GET)
  public String map() {
    return "main_content/map";
  }
}
