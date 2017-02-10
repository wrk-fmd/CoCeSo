package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.TriageService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/data/patadmin/triage", method = RequestMethod.GET)
public class TriageRestController {

  @Autowired
  private PatadminService patadminService;

  @Autowired
  private TriageService triageService;

  private final EntityEventHandler<Unit> entityEventHandler;

  @Autowired
  public TriageRestController(EntityEventFactory eehf) {
    this.entityEventHandler = eehf.getEntityEventHandler(Unit.class);
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTriage')")
  @Transactional
  @JsonView(JsonViews.Patadmin.class)
  @RequestMapping(value = "patients", produces = "application/json", method = RequestMethod.GET)
  public List<Patient> getPatients(@RequestParam("f") String f, @RequestParam("q") String q, @ActiveConcern Concern concern, @AuthenticationPrincipal User user) {
    return Initializer.incidents(triageService.getForAutocomplete(concern, q, f, user));
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTriage')")
  @JsonView(JsonViews.Patadmin.class)
  @RequestMapping(value = "groups", produces = "application/json", method = RequestMethod.GET)
  public SequencedResponse<List<Unit>> getGroups(@ActiveConcern Concern concern) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()), patadminService.getGroups(concern));
  }

}
