package at.wrk.coceso.radio;

import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import at.wrk.coceso.entityevent.EntityEventFactory;

@RestController
@RequestMapping("/data/radio")
@PreAuthorize("@auth.hasAccessLevel('Main')")
public class RadioController {

  @Autowired
  private RadioService radioService;

  private final EntityEventHandler<Selcall> entityEventHandler;

  @Autowired
  public RadioController(EntityEventFactory eehf) {
    this.entityEventHandler = eehf.getEntityEventHandler(Selcall.class);
  }

  @RequestMapping(value = "send", method = RequestMethod.POST, produces = "application/json")
  public RestResponse send(@RequestBody Selcall selcall) {
    return new RestResponse(radioService.sendSelcall(selcall));
  }

  @RequestMapping(value = "ports", method = RequestMethod.GET, produces = "application/json")
  public List<Port> ports() {
    return radioService.getPorts();
  }

  @RequestMapping(value = "reloadPorts", method = RequestMethod.POST, produces = "application/json")
  public RestResponse reloadPorts() {
    return new RestResponse(radioService.reloadPorts());
  }

  @RequestMapping(value = "getLast/{minutes}", method = RequestMethod.GET, produces = "application/json")
  public SequencedResponse<List<Selcall>> getLast(@PathVariable("minutes") int minutes) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(0), radioService.getLastMinutes(minutes));
  }
}
