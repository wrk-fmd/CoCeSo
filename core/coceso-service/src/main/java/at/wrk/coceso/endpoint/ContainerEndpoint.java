package at.wrk.coceso.endpoint;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.service.ContainerService;
import at.wrk.coceso.service.ContainerWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("@auth.hasAccessLevel('Edit')")
@RequestMapping("/data/container")
public class ContainerEndpoint {

  @Autowired
  private ContainerService containerService;

  @Autowired
  private ContainerWriteService containerWriteService;

  private final EntityEventHandler<Container> entityEventHandler;

  @Autowired
  public ContainerEndpoint(EntityEventFactory eehf) {
    this.entityEventHandler = eehf.getEntityEventHandler(Container.class);
  }

  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  public SequencedResponse<List<Container>> getAll(@ActiveConcern Concern concern) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()), containerService.getAll(concern));
  }

  @RequestMapping(value = "updateContainer", produces = "application/json", method = RequestMethod.POST)
  public RestResponse updateContainer(@RequestBody Container container, @ActiveConcern Concern concern) {
    container = containerWriteService.update(container, concern);
    return new RestResponse(true, new RestProperty("id", container.getId()));
  }

  @RequestMapping(value = "removeContainer", produces = "application/json", method = RequestMethod.POST)
  public RestResponse removeContainer(@RequestParam("container_id") int containerId) {
    containerWriteService.remove(containerId);
    return new RestResponse(true);
  }

  @RequestMapping(value = "updateUnit", produces = "application/json", method = RequestMethod.POST)
  public RestResponse updateUnit(@RequestParam("container_id") int containerId,
      @RequestParam("unit_id") int unitId, @RequestParam("ordering") double ordering) {
    containerWriteService.updateUnit(containerId, unitId, ordering);
    return new RestResponse(true);
  }

  @RequestMapping(value = "removeUnit", produces = "application/json", method = RequestMethod.POST)
  public RestResponse removeUnit(@RequestParam("unit_id") int unitId) {
    containerWriteService.removeUnit(unitId);
    return new RestResponse(true);
  }

}
