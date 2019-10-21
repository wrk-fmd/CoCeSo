package at.wrk.coceso.radio.endpoint;

import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.entity.helper.SequencedResponse;
import at.wrk.coceso.entityevent.EntityEventFactory;
import at.wrk.coceso.entityevent.EntityEventHandler;
import at.wrk.coceso.radio.entity.Port;
import at.wrk.coceso.radio.entity.RadioCall;
import at.wrk.coceso.radio.service.RadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data/radio")
@PreAuthorize("@auth.hasAccessLevel('Main')")
public class RadioEndpointImpl {

  @Autowired
  private RadioService radioService;

  private final EntityEventHandler<RadioCall> entityEventHandler;

  @Autowired
  public RadioEndpointImpl(EntityEventFactory entityEventFactory) {
    this.entityEventHandler = entityEventFactory.getEntityEventHandler(RadioCall.class);
  }

  @RequestMapping(value = "send", method = RequestMethod.POST, produces = "application/json")
  public RestResponse send(@RequestBody RadioCall selcall) {
    return new RestResponse(radioService.sendCall(selcall));
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
  public SequencedResponse<List<RadioCall>> getLast(@PathVariable("minutes") int minutes) {
    return new SequencedResponse<>(entityEventHandler.getHver(), entityEventHandler.getSeq(0), radioService.getLastMinutes(minutes));
  }
}
