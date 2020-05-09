package at.wrk.coceso.controller.data;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("@auth.hasAccessLevel('Edit')")
@RequestMapping("/data/container")
public class ContainerController {

  @Autowired
  private ContainerService containerService;

  @Autowired
  private ContainerWriteService containerWriteService;

  private final EntityEventHandler<Container> entityEventHandler;

  @Autowired
  public ContainerController(final EntityEventFactory entityEventFactory) {
    this.entityEventHandler = entityEventFactory.getEntityEventHandler(Container.class);
  }

  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  public SequencedResponse<List<Container>> getAll(@ActiveConcern final Concern concern) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(concern.getId()), containerService.getAll(concern));
  }

  @RequestMapping(value = "getAllForConcern", produces = "application/json", method = RequestMethod.GET)
  public RestResponse getAll(@RequestParam(value = "concernId") final int concernId) {
    return new RestResponse(true, new RestProperty("container", containerService.getAll(concernId)));
  }

  @RequestMapping(value = "updateContainer", produces = "application/json", method = RequestMethod.POST)
  public RestResponse updateContainer(@RequestBody final Container container, @ActiveConcern final Concern concern) {
    Container updatedContainer = containerWriteService.update(container, concern);
    return new RestResponse(true, new RestProperty("id", updatedContainer.getId()));
  }

  @RequestMapping(value = "removeContainer", produces = "application/json", method = RequestMethod.POST)
  public RestResponse removeContainer(@RequestParam("container_id") final int containerId) {
    containerWriteService.remove(containerId);
    return new RestResponse(true);
  }

  @RequestMapping(value = "updateUnit", produces = "application/json", method = RequestMethod.POST)
  public RestResponse updateUnit(
          @RequestParam("container_id") final int containerId,
          @RequestParam("unit_id") final int unitId,
          @RequestParam("ordering") final double ordering) {
    containerWriteService.updateUnit(containerId, unitId, ordering);
    return new RestResponse(true);
  }

  @RequestMapping(value = "removeUnit", produces = "application/json", method = RequestMethod.POST)
  public RestResponse removeUnit(@RequestParam("unit_id") final int unitId) {
    containerWriteService.removeUnit(unitId);
    return new RestResponse(true);
  }

}
