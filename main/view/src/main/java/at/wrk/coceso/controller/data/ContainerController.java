package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.ContainerService;
import at.wrk.coceso.service.ContainerSocketService;
import at.wrk.coceso.utils.ActiveConcern;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("@auth.hasAccessLevel('Edit')")
@RequestMapping("/data/container")
public class ContainerController {

  @Autowired
  private ContainerService containerService;

  @Autowired
  private ContainerSocketService containerSocketService;

  private final EntityEventHandler<Container> entityEventHandler = EntityEventHandler.getInstance(Container.class);

  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  public SequencedResponse<List<Container>> getAll(@ActiveConcern Concern concern) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()), containerService.getAll(concern));
  }

  @RequestMapping(value = "updateContainer", produces = "application/json", method = RequestMethod.POST)
  public RestResponse updateContainer(@RequestBody Container container, @ActiveConcern Concern concern) {
    container = containerSocketService.update(container, concern);
    return new RestResponse(true, new RestProperty("id", container.getId()));
  }

  @RequestMapping(value = "removeContainer", produces = "application/json", method = RequestMethod.POST)
  public RestResponse removeContainer(@RequestParam("container_id") int containerId) {
    containerSocketService.remove(containerId);
    return new RestResponse(true);
  }

  @RequestMapping(value = "updateUnit", produces = "application/json", method = RequestMethod.POST)
  public RestResponse updateUnit(@RequestParam("container_id") int containerId,
      @RequestParam("unit_id") int unitId, @RequestParam("ordering") double ordering) {
    containerSocketService.updateUnit(containerId, unitId, ordering);
    return new RestResponse(true);
  }

  @RequestMapping(value = "removeUnit", produces = "application/json", method = RequestMethod.POST)
  public RestResponse removeUnit(@RequestParam("unit_id") int unitId) {
    containerSocketService.removeUnit(unitId);
    return new RestResponse(true);
  }

}
