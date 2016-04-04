package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.PatientSocketService;
import at.wrk.coceso.utils.ActiveConcern;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("@auth.hasAccessLevel('Main')")
@RestController
@RequestMapping("/data/patient")
public class PatientController {

  private final EntityEventHandler<Patient> entityEventHandler = EntityEventHandler.getInstance(Patient.class);

  @Autowired
  private PatientService patientService;

  @Autowired
  private PatientSocketService patientSocketService;

  @JsonView(JsonViews.Main.class)
  @RequestMapping(value = "main", produces = "application/json", method = RequestMethod.GET)
  public SequencedResponse<List<Patient>> getForMain(@ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()), patientService.getAll(concern, user));
  }

  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  public RestResponse update(@RequestBody Patient patient, BindingResult result,
      @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    if (result.hasErrors()) {
      return new RestResponse(result);
    }

    boolean isNew = patient.getId() == null;
    patient = patientSocketService.update(patient, concern, user);
    return new RestResponse(true, new RestProperty("new", isNew), new RestProperty("patient_id", patient.getId()));
  }

}
