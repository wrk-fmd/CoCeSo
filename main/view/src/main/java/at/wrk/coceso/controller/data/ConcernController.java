package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
import at.wrk.coceso.validator.ConcernValidator;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data/concern")
public class ConcernController {

  private static final Logger LOG = LoggerFactory.getLogger(ConcernController.class);

  @Autowired
  private ConcernService concernService;

  @Autowired
  private ConcernValidator concernValidator;

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(concernValidator);
  }

  @PreAuthorize("isAuthenticated()")
  @JsonView(JsonViews.Home.class)
  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  public List<Concern> getAll() {
    return concernService.getAll();
  }

  @PreAuthorize("@auth.hasAccessLevel('Edit')")
  @Transactional
  @JsonView(JsonViews.Edit.class)
  @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
  public Concern get(@PathVariable("id") int concern_id) {
    return Initializer.init(concernService.getById(concern_id), Concern::getSections);
  }

  @PreAuthorize("@auth.hasAccessLevel('Edit')")
  @JsonView(JsonViews.Edit.class)
  @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.GET)
  public Concern getByCookie(@ActiveConcern(sections = true) Concern concern) {
    return concern;
  }

  @PreAuthorize("@auth.hasAccessLevel('Edit')")
  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  public RestResponse update(@RequestBody @Validated @Valid Concern concern, BindingResult result,
      @AuthenticationPrincipal User user) {
    LOG.debug("{}: Update request for concern {}", user, concern);

    if (result.hasErrors()) {
      return new RestResponse(result);
    }
    concern = concernService.update(concern, user);
    return new RestResponse(true, new RestProperty("id", concern.getId()));
  }

  @PreAuthorize("@auth.hasAccessLevel('CloseConcern')")
  @RequestMapping(value = "close", produces = "application/json", method = RequestMethod.POST)
  public RestResponse close(@RequestParam("concern_id") int concern_id,
      @AuthenticationPrincipal User user) {
    concernService.setClosed(concern_id, true, user);
    return new RestResponse(true);
  }

  @PreAuthorize("@auth.hasAccessLevel('CloseConcern')")
  @RequestMapping(value = "reopen", produces = "application/json", method = RequestMethod.POST)
  public RestResponse reopen(@RequestParam("concern_id") int concern_id,
      @AuthenticationPrincipal User user) {
    concernService.setClosed(concern_id, false, user);
    return new RestResponse(true);
  }

  @PreAuthorize("@auth.hasAccessLevel('Edit')")
  @RequestMapping(value = "addSection", produces = "application/json", method = RequestMethod.POST)
  public RestResponse addSection(@RequestParam("section") String section, @RequestParam("concern") int concern) {
    concernService.addSection(section, concern);
    return new RestResponse(true);
  }

  @PreAuthorize("@auth.hasAccessLevel('Edit')")
  @RequestMapping(value = "removeSection", produces = "application/json", method = RequestMethod.POST)
  public RestResponse removeSection(@RequestParam("section") String section, @RequestParam("concern") int concern) {
    concernService.removeSection(section, concern);
    return new RestResponse(true);
  }

}
