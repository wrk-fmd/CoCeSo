package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.LogEntry;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.enums.LogEntryType;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.*;
import at.wrk.coceso.utils.CocesoLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/search/patient")
public class SearchPatientController {

    @Autowired
    PatientService patientService;

    @Autowired
    IncidentService incidentService;

    @Autowired
    LogService logService;

    @Autowired
    UnitService unitService;

    @Autowired
    ConcernService concernService;

    @RequestMapping("")
    public String index_(ModelMap map) {
        // Concern List for <select>
        map.addAttribute("concerns", concernService.getAllActive());
        return "search/patient";
    }

    @RequestMapping("/{concernId}")
    public String index(@PathVariable(value = "concernId") String cid, ModelMap map) {
        int concernId;
        try {
            concernId = Integer.parseInt(cid);
            map.addAttribute("active", concernId);
        } catch (NumberFormatException ne) {
            return "redirect:/search/patient";
        }

        // Concern List for <select>
        map.addAttribute("concerns", concernService.getAllActive());
        return "search/patient";
    }

    @RequestMapping(value = "data/{concernId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<PatientModel> index(@PathVariable("concernId") String cid, Principal principal) {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        Operator user = (Operator) token.getPrincipal();

        int concernId;
        try {
            concernId = Integer.parseInt(cid);
        } catch (NumberFormatException ne) {
            CocesoLogger.debug(ne.getMessage());
            return new LinkedList<PatientModel>();
        }

        CocesoLogger.info("SearchPatient: User " + user.getUsername() + " loaded patientdata of concern #" + concernId);

        List<Patient> patients = patientService.getAll(concernId);

        List<PatientModel> models = new LinkedList<PatientModel>();

        for(Patient p : patients) {
            PatientModel tmp = new PatientModel(p);
            Incident i = incidentService.getById(p.getId());
            tmp.ao = i.getAo() == null ? null : i.getAo().getInfo();
            tmp.history = new LinkedList<HistoryModel>();

            for(LogEntry log : logService.getByIncidentId(i.getId())) {
                if(log.getType() == LogEntryType.TASKSTATE_CHANGED) {
                    HistoryModel model = new HistoryModel();
                    model.timestamp = log.getTimestamp();
                    model.state = log.getState();
                    model.unit_call = log.getUnit() == null ? null : log.getUnit().getCall();
                    tmp.history.add(model);
                }
            }
            models.add(tmp);
        }

        return models;
    }

    public class PatientModel {
        PatientModel(Patient p) {
            this.given_name = p.getGiven_name();
            this.sur_name = p.getSur_name();
            this.insurance_number = p.getInsurance_number();
            this.externalID = p.getExternalID();
            this.diagnosis = p.getDiagnosis();
            this.erType = p.getErType();
            this.info = p.getInfo();
        }

        String given_name;
        String sur_name;
        String insurance_number;
        String externalID;
        String diagnosis;
        String erType;
        String info;

        String ao;
        List<HistoryModel> history;

        public String getInfo() {
            return info;
        }

        public String getGiven_name() {
            return given_name;
        }

        public String getSur_name() {
            return sur_name;
        }

        public String getInsurance_number() {
            return insurance_number;
        }

        public String getExternalID() {
            return externalID;
        }

        public String getDiagnosis() {
            return diagnosis;
        }

        public String getErType() {
            return erType;
        }

        public String getAo() {
            return ao;
        }

        public List<HistoryModel> getHistory() {
            return history;
        }
    }
    public class HistoryModel {
        Timestamp timestamp;
        String unit_call;
        TaskState state;

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public String getUnit_call() {
            return unit_call;
        }

        public TaskState getState() {
            return state;
        }
    }
}
