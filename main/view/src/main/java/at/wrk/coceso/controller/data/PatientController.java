package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.PatientService;
import at.wrk.coceso.service.PatientWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("@auth.hasAccessLevel('Main')")
@RestController
@RequestMapping("/data/patient")
public class PatientController {

  private final EntityEventHandler<Patient> entityEventHandler;

  @Autowired
  public PatientController(EntityEventFactory entityEventFactory) {
    this.entityEventHandler = entityEventFactory.getEntityEventHandler(Patient.class);
  }

  @Autowired
  private PatientService patientService;

  @Autowired
  private PatientWriteService patientWriteService;

  @JsonView(JsonViews.Main.class)
  @RequestMapping(value = "main", produces = "application/json", method = RequestMethod.GET)
  public SequencedResponse<List<Patient>> getForMain(
          @ActiveConcern final Concern concern,
          @AuthenticationPrincipal final User user) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()), patientService.getAll(concern, user));
  }

  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  public RestResponse update(
          @RequestBody final Patient patient,
          final BindingResult result,
          @ActiveConcern final Concern concern,
          @AuthenticationPrincipal final User user) {
    if (result.hasErrors()) {
      return new RestResponse(result);
    }

    boolean isNew = patient.getId() == null;
    Patient updatedPatient = patientWriteService.update(patient, concern, user);
    return new RestResponse(true, new RestProperty("new", isNew), new RestProperty("patient_id", updatedPatient.getId()));
  }

}
