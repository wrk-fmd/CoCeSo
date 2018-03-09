package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.TransportForm;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.PostprocessingService;
import at.wrk.coceso.service.patadmin.PostprocessingWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
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
@RequestMapping(value = "/patadmin/postprocessing", method = RequestMethod.GET)
public class PostprocessingController {

  @Autowired
  private PatientService patientService;

  @Autowired
  private PatadminService patadminService;

  @Autowired
  private PostprocessingService postprocessingService;

  @Autowired
  private PostprocessingWriteService postprocessingWriteService;

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminPostprocessing')")
  @Transactional
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String showHome(ModelMap map, @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    map.addAttribute("patients", Initializer.initGroups(patadminService.getAllInTreatment(concern, user)));
    patadminService.addAccessLevels(map, concern);
    return "patadmin/postprocessing/list";
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminPostprocessing')")
  @Transactional
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public String showSearch(ModelMap map, @ActiveConcern Concern concern, @RequestParam("q") String query, @AuthenticationPrincipal User user) {
    map.addAttribute("patients", Initializer.initGroups(patadminService.getPatientsByQuery(concern, query, true, user)));
    map.addAttribute("search", query);
    patadminService.addAccessLevels(map, concern);
    return "patadmin/postprocessing/list";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @Transactional
  @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
  public String showPatient(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = Initializer.initGroups(patientService.getById(id, user));
    map.addAttribute("patient", patient);
    patadminService.addAccessLevels(map, patient.getConcern());
    return "patadmin/postprocessing/view";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
  public ModelAndView showEdit(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = patientService.getById(id, user);
    patadminService.addAccessLevels(map, patient.getConcern());
    return new ModelAndView("patadmin/postprocessing/form", "command", new PostprocessingForm(patient));
  }

  @PreAuthorize("@auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(@ModelAttribute PostprocessingForm form, @AuthenticationPrincipal User user) {
    Patient patient = postprocessingWriteService.update(form, user);
    return String.format("redirect:/patadmin/postprocessing/view/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/discharge/{id}", method = RequestMethod.GET)
  public ModelAndView showDischarge(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = postprocessingService.getActivePatient(id, user);
    patadminService.addAccessLevels(map, patient.getConcern());
    return new ModelAndView("patadmin/postprocessing/discharge", "command", new PostprocessingForm(patient));
  }

  @PreAuthorize("@auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/discharge", method = RequestMethod.POST)
  public String saveDischarge(@ModelAttribute PostprocessingForm form, @AuthenticationPrincipal User user) {
    Patient patient = postprocessingWriteService.discharge(form, user);
    return String.format("redirect:/patadmin/postprocessing/view/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/transport/{id}", method = RequestMethod.GET)
  public ModelAndView showTransport(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = postprocessingService.getActivePatient(id, user);
    patadminService.addAccessLevels(map, patient.getConcern());
    return new ModelAndView("patadmin/postprocessing/transport", "command", new TransportForm(patient));
  }

  @PreAuthorize("@auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/transport", method = RequestMethod.POST)
  public String requestTransport(@ModelAttribute TransportForm form, @AuthenticationPrincipal User user) {
    Patient patient = postprocessingWriteService.transport(form, user);
    return String.format("redirect:/patadmin/postprocessing/view/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/transported/{id}", method = RequestMethod.GET)
  public String showTransported(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = postprocessingService.getTransported(id, user);
    map.addAttribute("patient", patient);
    patadminService.addAccessLevels(map, patient.getConcern());
    return "patadmin/postprocessing/transported";
  }

  @PreAuthorize("@auth.hasPermission(#patientId, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/transported", method = RequestMethod.POST)
  public String saveTransported(@RequestParam("patient") int patientId, @AuthenticationPrincipal User user) {
    Patient patient = postprocessingWriteService.transported(patientId, user);
    return String.format("redirect:/patadmin/postprocessing/view/%d", patient.getId());
  }

}
