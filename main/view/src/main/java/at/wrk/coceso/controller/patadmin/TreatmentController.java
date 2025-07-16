package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.form.PostprocessingForm;
import at.wrk.coceso.form.RegistrationForm;
import at.wrk.coceso.form.TransportForm;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.PostprocessingService;
import at.wrk.coceso.service.patadmin.PostprocessingWriteService;
import at.wrk.coceso.service.patadmin.RegistrationService;
import at.wrk.coceso.service.patadmin.RegistrationWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
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
@RequestMapping(value = "/patadmin/treatment", method = RequestMethod.GET)
public class TreatmentController {

    private final PatientService patientService;
    private final PatadminService patadminService;
    private final LogService logService;
    private final PostprocessingService postprocessingService;
    private final PostprocessingWriteService postprocessingWriteService;
    private final RegistrationService registrationService;
    private final RegistrationWriteService registrationWriteService;

    public TreatmentController(
        PatientService patientService,
        PatadminService patadminService,
        LogService logService,
        PostprocessingService postprocessingService,
        PostprocessingWriteService postprocessingWriteService,
        RegistrationService registrationService,
        RegistrationWriteService registrationWriteService
    ) {
        this.patientService = patientService;
        this.patadminService = patadminService;
        this.logService = logService;
        this.postprocessingService = postprocessingService;
        this.postprocessingWriteService = postprocessingWriteService;
        this.registrationService = registrationService;
        this.registrationWriteService = registrationWriteService;
    }

    @ModelAttribute("viewType")
    public String viewType() {
        return "treatment";
    }

    @ModelAttribute("showSearch")
    public boolean showSearch() {
        return true;
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTreatment')")
    @Transactional
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showHome(final ModelMap map, @ActiveConcern final Concern concern, @RequestParam(required = false) Integer saved) {
        map.addAttribute("patients", Initializer.initGroups(patadminService.getAllInTreatment(concern)));
        map.addAttribute("treatmentCount", registrationService.getTreatmentCount(concern));
        map.addAttribute("transportCount", registrationService.getTransportCount(concern));
        map.addAttribute("savedPatientId", saved);
        patadminService.addAccessLevels(map, concern);
        return "patadmin/treatment/home";
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTreatment')")
    @Transactional
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String showList(final ModelMap map, @ActiveConcern final Concern concern) {
        map.addAttribute("patients", Initializer.initGroups(patadminService.getAll(concern)));
        patadminService.addAccessLevels(map, concern);
        return "patadmin/postprocessing/list";
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTreatment')")
    @Transactional
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String showSearch(final ModelMap map, @ActiveConcern final Concern concern, @RequestParam("q") final String query) {
        map.addAttribute("patients", Initializer.initGroups(patadminService.getPatientsByQuery(concern, query, true)));
        map.addAttribute("search", query);
        patadminService.addAccessLevels(map, concern);
        return "patadmin/postprocessing/list";
    }

    @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')")
    @Transactional
    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String showPatient(final ModelMap map, @PathVariable final int id) {
        Patient patient = Initializer.initGroups(patientService.getById(id));
        map.addAttribute("patient", patient);
        map.addAttribute("logs", logService.getPatientLogsFilteredByOverviewStates(patient));
        patadminService.addAccessLevels(map, patient.getConcern());
        return "patadmin/postprocessing/view";
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTreatment')")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView showAdd(final ModelMap map, @ActiveConcern final Concern concern) {
        map.addAttribute("groups", patadminService.getGroups(concern));
        patadminService.addAccessLevels(map, concern);
        return new ModelAndView("patadmin/treatment/form", "command", new RegistrationForm());
    }

    @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')")
    @Transactional
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public ModelAndView showEdit(final ModelMap map, @PathVariable final int id) {
        Patient patient = patientService.getById(id);
        if (!patient.isDone()) {
            map.addAttribute("groups", patadminService.getGroups(patient.getConcern()));
        }
        patadminService.addAccessLevels(map, patient.getConcern());
        return new ModelAndView("patadmin/treatment/form", "command", new RegistrationForm(patient));
    }

    @PreAuthorize("#form.patient != null"
        + " ? @auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')"
        + " : @auth.hasPermission(#concern, 'PatadminTreatment')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(
        @ModelAttribute final RegistrationForm form,
        @ActiveConcern final Concern concern,
        @RequestParam(defaultValue = "false") boolean transport
    ) {
        Patient patient = registrationWriteService.update(form, concern, true);
        return transport
            ? String.format("redirect:/patadmin/treatment/transport/%d", patient.getId())
            : String.format("redirect:/patadmin/treatment?saved=%d", patient.getId());
    }

    @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')")
    @RequestMapping(value = "/discharge/{id}", method = RequestMethod.GET)
    public ModelAndView showDischarge(final ModelMap map, @PathVariable final int id) {
        Patient patient = postprocessingService.getActivePatient(id);
        patadminService.addAccessLevels(map, patient.getConcern());
        return new ModelAndView("patadmin/postprocessing/discharge", "command", new PostprocessingForm(patient));
    }

    @PreAuthorize("@auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')")
    @RequestMapping(value = "/discharge", method = RequestMethod.POST)
    public String saveDischarge(@ModelAttribute final PostprocessingForm form) {
        Patient patient = postprocessingWriteService.discharge(form);
        return String.format("redirect:/patadmin/treatment/view/%d", patient.getId());
    }

    @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')")
    @RequestMapping(value = "/transport/{id}", method = RequestMethod.GET)
    public ModelAndView showTransport(final ModelMap map, @PathVariable final int id) {
        Patient patient = postprocessingService.getActivePatient(id);
        patadminService.addAccessLevels(map, patient.getConcern());
        return new ModelAndView("patadmin/postprocessing/transport", "command", new TransportForm(patient));
    }

    @PreAuthorize("@auth.hasPermission(#form.patient, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')")
    @RequestMapping(value = "/transport", method = RequestMethod.POST)
    public String requestTransport(@ModelAttribute final TransportForm form) {
        Patient patient = postprocessingWriteService.transport(form);
        return String.format("redirect:/patadmin/treatment/view/%d", patient.getId());
    }

    @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')")
    @RequestMapping(value = "/transported/{id}", method = RequestMethod.GET)
    public String showTransported(final ModelMap map, @PathVariable final int id) {
        Patient patient = postprocessingService.getTransported(id);
        map.addAttribute("patient", patient);
        patadminService.addAccessLevels(map, patient.getConcern());
        return "patadmin/postprocessing/transported";
    }

    @PreAuthorize("@auth.hasPermission(#patientId, 'at.wrk.coceso.entity.Patient', 'PatadminTreatment')")
    @RequestMapping(value = "/transported", method = RequestMethod.POST)
    public String saveTransported(@RequestParam("patient") final int patientId) {
        Patient patient = postprocessingWriteService.transported(patientId);
        return String.format("redirect:/patadmin/treatment/view/%d", patient.getId());
    }
}
