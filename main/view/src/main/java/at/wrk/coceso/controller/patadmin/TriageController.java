package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.form.TriageForm;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.patadmin.MedinfoService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.TriageService;
import at.wrk.coceso.service.patadmin.TriageWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
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
@RequestMapping(value = "/patadmin/triage", method = RequestMethod.GET)
public class TriageController {

  @Autowired
  private PatientService patientService;

  @Autowired
  private PatadminService patadminService;

  @Autowired
  private TriageService triageService;

  @Autowired
  private TriageWriteService triageWriteService;

  @Autowired
  private MedinfoService medinfoService;

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTriage')")
  @Transactional
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String showHome(ModelMap map, @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    patadminService.addAccessLevels(map, concern);
    map.addAttribute("incoming", triageService.getIncoming(concern));
    map.addAttribute("treatment", Initializer.incidents(patadminService.getAllInTreatment(concern, user)));
    return "patadmin/triage/home";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Unit', 'PatadminTriage')")
  @RequestMapping(value = "/group/{id}", method = RequestMethod.GET)
  public String showGroup(ModelMap map, @PathVariable int id) {
    Unit group = patadminService.getGroup(id);

    patadminService.addAccessLevels(map, group.getConcern());
    map.addAttribute("group", group);
    map.addAttribute("incoming", triageService.getIncoming(group));
    map.addAttribute("treatment", group.getIncidents().keySet().stream()
        .map(Incident::getPatient)
        .filter(Objects::nonNull)
        .collect(Collectors.toList()));
    return "patadmin/triage/group";
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTriage')")
  @Transactional
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public String showSearch(ModelMap map, @ActiveConcern Concern concern, @RequestParam("q") String query, @AuthenticationPrincipal User user) {
    patadminService.addAccessLevels(map, concern);
    map.addAttribute("patients", Initializer.incidents(patadminService.getPatientsByQuery(concern, query, false, user)));
    map.addAttribute("medinfos", medinfoService.getAllByQuery(concern, query, user));
    map.addAttribute("search", query);
    return "patadmin/triage/search";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminTriage')")
  @Transactional
  @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
  public String showPatient(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = patientService.getById(id, user);

    patadminService.addAccessLevels(map, patient.getConcern());
    patient.getIncidents().size();
    map.addAttribute("patient", patient);
    return "patadmin/triage/view";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Medinfo', 'PatadminTriage')")
  @Transactional
  @RequestMapping(value = "/medinfo/{id}", method = RequestMethod.GET)
  public String showMedinfo(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Medinfo medinfo = medinfoService.getById(id, user);

    patadminService.addAccessLevels(map, medinfo.getConcern());
    Initializer.incidents(medinfo.getPatients());
    map.addAttribute("medinfo", medinfo);
    return "patadmin/triage/medinfo";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminTriage')")
  @Transactional
  @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
  public ModelAndView showEdit(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = triageService.getActivePatient(id, user);

    patadminService.addAccessLevels(map, patient.getConcern());
    map.addAttribute("groups", patadminService.getGroups(patient.getConcern()));
    return new ModelAndView("patadmin/triage/form", "command", new TriageForm(patient));
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Incident', 'PatadminTriage')")
  @RequestMapping(value = "/takeover/{id}", method = RequestMethod.GET)
  public String showTakeover(@PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = triageWriteService.takeover(id, user);
    return String.format("redirect:/patadmin/triage/edit/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTriage')")
  @Transactional
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(@ModelAttribute TriageForm form, @ActiveConcern Concern concern,
      @AuthenticationPrincipal User user) {
    Patient patient = triageWriteService.update(form, concern, user);
    return String.format("redirect:/patadmin/triage/view/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTriage')")
  @RequestMapping(value = "/add", method = RequestMethod.GET)
  public ModelAndView showAdd(ModelMap map, @RequestParam(value = "group", required = false) Integer group,
      @RequestParam(value = "medinfo", required = false) Integer medinfoId, @ActiveConcern Concern concern,
      @AuthenticationPrincipal User user) {
    Medinfo medinfo = null;
    if (medinfoId != null) {
      medinfo = medinfoService.getById(medinfoId, user);
    }

    TriageForm form = new TriageForm(medinfo);
    form.setGroup(group);

    patadminService.addAccessLevels(map, concern);
    map.addAttribute("groups", patadminService.getGroups(concern));
    return new ModelAndView("patadmin/triage/form", "command", form);
  }

}
