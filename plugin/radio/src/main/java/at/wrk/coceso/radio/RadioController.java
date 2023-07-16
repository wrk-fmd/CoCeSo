package at.wrk.coceso.radio;

import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data/radio")
@PreAuthorize("@auth.hasAccessLevel('Main')")
public class RadioController {

  @Autowired
  private RadioService radioService;

  private final EntityEventHandler<Selcall> entityEventHandler;

  @Autowired
  public RadioController(EntityEventFactory entityEventFactory) {
    this.entityEventHandler = entityEventFactory.getEntityEventHandler(Selcall.class);
  }

  @RequestMapping(value = "send", method = RequestMethod.POST, produces = "application/json")
  public RestResponse send(@RequestBody Selcall selcall) {
    return new RestResponse(false, new RestProperty("info", "Not implemented."));
  }

  @RequestMapping(value = "ports", method = RequestMethod.GET, produces = "application/json")
  public List<Port> ports() {
    return radioService.getPorts();
  }

  @RequestMapping(value = "reloadPorts", method = RequestMethod.POST, produces = "application/json")
  public RestResponse reloadPorts() {
    return new RestResponse(true, new RestProperty("info", "Not implemented."));
  }

  @RequestMapping(value = "getLast/{minutes}", method = RequestMethod.GET, produces = "application/json")
  public SequencedResponse<List<Selcall>> getLast(@PathVariable("minutes") int minutes) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(0), radioService.getLastMinutes(minutes));
  }
}
