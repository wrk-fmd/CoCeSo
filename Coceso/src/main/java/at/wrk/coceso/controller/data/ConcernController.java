package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.service.ConcernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/data/concern")
public class ConcernController {

  private static final Logger logger = Logger.getLogger("CoCeSo");

  @Autowired
  ConcernService concernService;

  @ResponseBody
  @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
  public List<Concern> getAll() {
    return concernService.getAll();
  }

  @ResponseBody
  @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
  public Concern get(@PathVariable("id") int concern_id) {
    return concernService.getById(concern_id);
  }

  @ResponseBody
  @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.GET)
  public Concern getByCookie(@CookieValue("concern") int concern_id) {
    return get(concern_id);
  }

  @ResponseBody
  @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
  public String update(@RequestBody Concern concern, BindingResult result, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    logger.log(Level.FINE, "Update request of Concern #{0} by User {1}", new Object[]{concern.getId(), user.getUsername()});

    if (result.hasErrors()) {
      return "{\"success\": false, description: \"Binding Error\"}";
    }

    boolean success = (concern.getId() > 0) ? concernService.update(concern, user) : (concernService.add(concern, user) != -3);

    return "{\"success\":" + success + (success ? "" : ",\"error\":6") + "}";
  }

}
