package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.form.RegistrationForm;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.RegistrationService;
import at.wrk.coceso.service.patadmin.RegistrationWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/patadmin/registration", method = RequestMethod.GET)
public class RegistrationController {

  @Autowired
  private PatientService patientService;

  @Autowired
  private PatadminService patadminService;

  @Autowired
  private RegistrationService registrationService;

  @Autowired
  private RegistrationWriteService registrationWriteService;

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminRegistration')")
  @Transactional
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String showHome(ModelMap map, @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    patadminService.addAccessLevels(map, concern);

    List<Incident> incoming = registrationService.getIncoming(concern);
    Initializer.init(incoming, Incident::getUnits);
    Initializer.init(incoming, Incident::getPatient);

    map.addAttribute("incoming", incoming);
    map.addAttribute("treatment", Initializer.initGroups(patadminService.getAllInTreatment(concern, user)));

    map.addAttribute("treatmentCount", registrationService.getTreatmentCount(concern));
    map.addAttribute("transportCount", registrationService.getTransportCount(concern));

    return "patadmin/registration/home";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Unit', 'PatadminRegistration')")
  @Transactional
  @RequestMapping(value = "/group/{id}", method = RequestMethod.GET)
  public String showGroup(ModelMap map, @PathVariable int id) {
    Unit group = patadminService.getGroup(id);

    patadminService.addAccessLevels(map, group.getConcern());
    map.addAttribute("group", group);

    List<Incident> incoming = registrationService.getIncoming(group);
    Initializer.init(incoming, Incident::getUnits);
    Initializer.init(incoming, Incident::getPatient);

    map.addAttribute("incoming", incoming);
    map.addAttribute("treatment", Initializer.init(group.getIncidents().keySet().stream()
        .map(Incident::getPatient)
        .filter(Objects::nonNull)
        .collect(Collectors.toList()), Patient::getId));
    return "patadmin/registration/group";
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminRegistration')")
  @Transactional
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public String showSearch(ModelMap map, @ActiveConcern Concern concern, @RequestParam("q") String query, @AuthenticationPrincipal User user) {
    patadminService.addAccessLevels(map, concern);
    map.addAttribute("patients", Initializer.initGroups(patadminService.getPatientsByQuery(concern, query, false, user)));
    map.addAttribute("search", query);
    return "patadmin/registration/search";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminRegistration')")
  @Transactional
  @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
  public String showPatient(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = Initializer.initGroups(patientService.getById(id, user));

    patadminService.addAccessLevels(map, patient.getConcern());
    map.addAttribute("patient", patient);
    return "patadmin/registration/view";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminRegistration')")
  @Transactional
  @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
  public ModelAndView showEdit(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = registrationService.getActivePatient(id, user);

    patadminService.addAccessLevels(map, patient.getConcern());
    map.addAttribute("groups", patadminService.getGroups(patient.getConcern()));
    return new ModelAndView("patadmin/registration/form", "command", new RegistrationForm(patient));
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Incident', 'PatadminRegistration')")
  @RequestMapping(value = "/takeover/{id}", method = RequestMethod.GET)
  public String showTakeover(@PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = registrationWriteService.takeover(id, user);
    return String.format("redirect:/patadmin/registration/edit/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminRegistration')")
  @Transactional
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(@ModelAttribute RegistrationForm form, @ActiveConcern Concern concern,
      @AuthenticationPrincipal User user) {
    Patient patient = registrationWriteService.update(form, concern, user);
    return "redirect:/patadmin/registration/add";
    //return String.format("redirect:/patadmin/registration/view/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminRegistration')")
  @RequestMapping(value = "/add", method = RequestMethod.GET)
  public ModelAndView showAdd(ModelMap map, @RequestParam(value = "group", required = false) Integer group,
      @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    RegistrationForm form = new RegistrationForm();
    form.setGroup(group);

    patadminService.addAccessLevels(map, concern);
    map.addAttribute("groups", patadminService.getGroups(concern));
    return new ModelAndView("patadmin/registration/form", "command", form);
  }

}
