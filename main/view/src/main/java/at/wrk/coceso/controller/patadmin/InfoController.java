package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.service.patadmin.InfoService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.utils.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@EnableSpringDataWebSupport
@RequestMapping(value = "/patadmin/info", method = RequestMethod.GET)
public class InfoController {

  @Autowired
  private PatientService patientService;

  @Autowired
  private PatadminService patadminService;

  @Autowired
  private LogService logService;

  @Autowired
  private InfoService infoService;

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminInfo')")
  @Transactional
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String showHome(ModelMap map, @PageableDefault(sort = "id", size = 20) Pageable pageable, @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    Page<Patient> patients = Initializer.init(infoService.getAll(concern, pageable, user), Patient::getIncidents);
    map.addAttribute("patients", patients);
    patadminService.addAccessLevels(map, concern);
    return "patadmin/info/list";
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminInfo')")
  @Transactional
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public String showSearch(ModelMap map, @PageableDefault(sort = "id", size = 20) Pageable pageable, @ActiveConcern Concern concern, @RequestParam("q") String query,
      @AuthenticationPrincipal User user) {
    Page<Patient> patients = Initializer.init(infoService.getByQuery(concern, query, pageable, user), Patient::getIncidents);
    map.addAttribute("patients", patients);
    map.addAttribute("search", query);
    patadminService.addAccessLevels(map, concern);
    return "patadmin/info/list";
  }

  @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminInfo')")
  @Transactional
  @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
  public String showPatient(ModelMap map, @PathVariable int id, @AuthenticationPrincipal User user) {
    Patient patient = Initializer.init(patientService.getById(id, user), Patient::getIncidents);
    map.addAttribute("patient", patient);
    map.addAttribute("logs", logService.getStatesByPatient(patient));
    patadminService.addAccessLevels(map, patient.getConcern());
    return "patadmin/info/view";
  }

}
