package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Selcall;
import at.wrk.coceso.service.SelcallService;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Robert on 14.06.2014.
 */
@RestController
@RequestMapping("/data/radio")
public class SelcallController {

  @Autowired
  SelcallService selcallService;

  @RequestMapping(value = "send", method = RequestMethod.POST, produces = "application/json")
  public String send(@RequestBody Selcall selcall) {
    return String.format("{\"sucess\":%b}", selcallService.sendSelcall(selcall));
  }

  @RequestMapping(value = "ports", method = RequestMethod.GET, produces = "application/json")
  public Set<String> ports() {
    return selcallService.getPorts();
  }

  @RequestMapping(value = "getLast/{minutes}", method = RequestMethod.GET, produces = "application/json")
  public List<Selcall> getLast(@PathVariable("minutes") int minutes) {
    return selcallService.getLastMinutes(minutes);
  }
}
