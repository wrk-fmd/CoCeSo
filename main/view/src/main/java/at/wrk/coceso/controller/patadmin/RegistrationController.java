package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.form.RegistrationForm;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.RegistrationService;
import at.wrk.coceso.service.patadmin.RegistrationWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/patadmin/registration", method = RequestMethod.GET)
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    private final PatientService patientService;
    private final PatadminService patadminService;
    private final RegistrationService registrationService;
    private final RegistrationWriteService registrationWriteService;
    public static final boolean SHOW_INCOMING_INCIDENTS_WITHOUT_PATIENT = true;

    public RegistrationController(
            final PatientService patientService,
            final PatadminService patadminService,
            final RegistrationService registrationService,
            final RegistrationWriteService registrationWriteService) {
        this.patientService = patientService;
        this.patadminService = patadminService;
        this.registrationService = registrationService;
        this.registrationWriteService = registrationWriteService;
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'PatadminRegistration')")
    @Transactional
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showHome(final ModelMap map, @ActiveConcern final Concern concern) {
        patadminService.addAccessLevels(map, concern);

        List<Incident> incoming = registrationService.getIncoming(concern);
        List<Patient> patientsInAllTreatments = patadminService.getAllInTreatment(concern);
        addIncidentsWithIncomingPatientsAndNotYetTreatedToMap(map, incoming, patientsInAllTreatments);
        map.addAttribute("treatment", Initializer.initGroups(patientsInAllTreatments));

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
        List<Patient> patientsInTreatment = group
                .getIncidents()
                .keySet()
                .stream()
                .map(Incident::getPatient)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        addIncidentsWithIncomingPatientsAndNotYetTreatedToMap(map, incoming, patientsInTreatment);
        map.addAttribute("treatment", Initializer.init(patientsInTreatment, Patient::getId));
        return "patadmin/registration/group";
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'PatadminRegistration')")
    @Transactional
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String showSearch(
            final ModelMap map,
            @ActiveConcern final Concern concern,
            @RequestParam("q") final String query) {
        patadminService.addAccessLevels(map, concern);
        map.addAttribute("patients", Initializer.initGroups(patadminService.getPatientsByQuery(concern, query, false)));
        map.addAttribute("search", query);
        return "patadmin/registration/search";
    }

    @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminRegistration')")
    @Transactional
    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String showPatient(
            final ModelMap map,
            @PathVariable final int id) {
        Patient patient = Initializer.initGroups(patientService.getById(id));

        patadminService.addAccessLevels(map, patient.getConcern());
        map.addAttribute("patient", patient);
        return "patadmin/registration/view";
    }

    @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Patient', 'PatadminRegistration')")
    @Transactional
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public ModelAndView showEdit(
            final ModelMap map,
            @PathVariable final int id) {
        Patient patient = registrationService.getActivePatient(id);

        patadminService.addAccessLevels(map, patient.getConcern());
        map.addAttribute("groups", patadminService.getGroups(patient.getConcern()));
        return new ModelAndView("patadmin/registration/form", "command", new RegistrationForm(patient));
    }

    @PreAuthorize("@auth.hasPermission(#id, 'at.wrk.coceso.entity.Incident', 'PatadminRegistration')")
    @RequestMapping(value = "/takeover/{id}", method = RequestMethod.GET)
    public String showTakeover(@PathVariable final int id) {
        Patient patient = registrationWriteService.takeover(id);
        return String.format("redirect:/patadmin/registration/edit/%d", patient.getId());
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'PatadminRegistration')")
    @Transactional
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(
            @ModelAttribute final RegistrationForm form,
            @ActiveConcern final Concern concern) {
        Patient patient = registrationWriteService.update(form, concern);
        return "redirect:/patadmin/registration/add?successfullyCreated=true";
    }

    @PreAuthorize("@auth.hasPermission(#concern, 'PatadminRegistration')")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView showAdd(
            final ModelMap map,
            @RequestParam(value = "group", required = false) final Integer group,
            @RequestParam(value = "successfullyCreated", defaultValue = "false") final boolean successfullyCreated,
            @ActiveConcern final Concern concern) {
        RegistrationForm form = new RegistrationForm();
        form.setGroup(group);

        patadminService.addAccessLevels(map, concern);
        map.addAttribute("groups", patadminService.getGroups(concern));
        map.addAttribute("successfullyCreated", successfullyCreated);
        return new ModelAndView("patadmin/registration/form", "command", form);
    }


    private void addIncidentsWithIncomingPatientsAndNotYetTreatedToMap(
            final ModelMap map,
            final List<Incident> incoming,
            final Collection<Patient> patientsInTreatment) {
        Set<Integer> patientIdsInTreatment = patientsInTreatment.stream()
                .map(Patient::getId)
                .collect(Collectors.toSet());
        List<Incident> filteredIncomingPatients = incoming
                .stream()
                .filter(Objects::nonNull)
                .filter(incident -> Optional.ofNullable(incident.getPatient())
                        .map(Patient::getId)
                        .map(patientId -> !patientIdsInTreatment.contains(patientId))
                        .orElse(SHOW_INCOMING_INCIDENTS_WITHOUT_PATIENT))
                .collect(Collectors.toList());
        LOG.trace(
                "Filtering incoming incidents {} with patientsInTreatment {} and their IDs {} to filtered list {}.",
                incoming,
                patientsInTreatment,
                patientIdsInTreatment,
                filteredIncomingPatients);

        Initializer.init(filteredIncomingPatients, Incident::getUnits);
        Initializer.init(filteredIncomingPatients, Incident::getPatient);

        map.addAttribute("incoming", filteredIncomingPatients);
    }
}
