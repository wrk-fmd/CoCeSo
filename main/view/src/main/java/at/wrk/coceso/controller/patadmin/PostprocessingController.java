package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
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
  public String showHome(
          final ModelMap map,
          @ActiveConcern final Concern concern) {
    map.addAttribute("patients", Initializer.initGroups(patadminService.getAllInTreatment(concern)));
    patadminService.addAccessLevels(map, concern);
    return "patadmin/postprocessing/list";
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminPostprocessing')")
  @Transactional
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public String showSearch(
          final ModelMap map,
          @ActiveConcern final Concern concern,
          @RequestParam("q") final String query) {
    map.addAttribute("patients", Initializer.initGroups(patadminService.getPatientsByQuery(concern, query, true)));
    map.addAttribute("search", query);
    patadminService.addAccessLevels(map, concern);
    return "patadmin/postprocessing/list";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @Transactional
  @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
  public String showPatient(
          final ModelMap map,
          @PathVariable final int id) {
    Patient patient = Initializer.initGroups(patientService.getById(id));
    map.addAttribute("patient", patient);
    patadminService.addAccessLevels(map, patient.getConcern());
    return "patadmin/postprocessing/view";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
  public ModelAndView showEdit(
          final ModelMap map,
          @PathVariable final int id) {
    Patient patient = patientService.getById(id);
    patadminService.addAccessLevels(map, patient.getConcern());
    return new ModelAndView("patadmin/postprocessing/form", "command", new PostprocessingForm(patient));
  }

  @PreAuthorize("@auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(
          @ModelAttribute final PostprocessingForm form) {
    Patient patient = postprocessingWriteService.update(form);
    return String.format("redirect:/patadmin/postprocessing/view/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/discharge/{id}", method = RequestMethod.GET)
  public ModelAndView showDischarge(
          final ModelMap map,
          @PathVariable final int id) {
    Patient patient = postprocessingService.getActivePatient(id);
    patadminService.addAccessLevels(map, patient.getConcern());
    return new ModelAndView("patadmin/postprocessing/discharge", "command", new PostprocessingForm(patient));
  }

  @PreAuthorize("@auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/discharge", method = RequestMethod.POST)
  public String saveDischarge(
          @ModelAttribute final PostprocessingForm form) {
    Patient patient = postprocessingWriteService.discharge(form);
    return String.format("redirect:/patadmin/postprocessing/view/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/transport/{id}", method = RequestMethod.GET)
  public ModelAndView showTransport(
          final ModelMap map,
          @PathVariable final int id) {
    Patient patient = postprocessingService.getActivePatient(id);
    patadminService.addAccessLevels(map, patient.getConcern());
    return new ModelAndView("patadmin/postprocessing/transport", "command", new TransportForm(patient));
  }

  @PreAuthorize("@auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/transport", method = RequestMethod.POST)
  public String requestTransport(
          @ModelAttribute final TransportForm form) {
    Patient patient = postprocessingWriteService.transport(form);
    return String.format("redirect:/patadmin/postprocessing/view/%d", patient.getId());
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/transported/{id}", method = RequestMethod.GET)
  public String showTransported(
          final ModelMap map,
          @PathVariable final int id) {
    Patient patient = postprocessingService.getTransported(id);
    map.addAttribute("patient", patient);
    patadminService.addAccessLevels(map, patient.getConcern());
    return "patadmin/postprocessing/transported";
  }

  @PreAuthorize("@auth.hasPermission(#patientId, 'at.wrk.coceso.entity.Patient', 'PatadminPostprocessing')")
  @RequestMapping(value = "/transported", method = RequestMethod.POST)
  public String saveTransported(
          @RequestParam("patient") final int patientId) {
    Patient patient = postprocessingWriteService.transported(patientId);
    return String.format("redirect:/patadmin/postprocessing/view/%d", patient.getId());
  }
}
