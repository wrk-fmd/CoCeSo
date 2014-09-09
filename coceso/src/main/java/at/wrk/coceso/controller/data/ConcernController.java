package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Operator;
import at.wrk.coceso.entity.enums.CocesoAuthority;
import at.wrk.coceso.service.ConcernService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/data/concern")
public class ConcernController {

  private static final Logger logger = Logger.getLogger(ConcernController.class);

  // TODO Authority to Close and Reopen Concerns
  private static final CocesoAuthority CLOSE_AUTHORITY = CocesoAuthority.Root;

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

    logger.debug("Update request of Concern # " + concern.getId() + " by User " + user.getUsername());

    if (result.hasErrors()) {
      return "{\"success\": false, description: \"Binding Error\"}";
    }

    boolean success = (concern.getId() > 0) ? concernService.update(concern, user) : (concernService.add(concern, user) != -3);

    return "{\"success\":" + success + (success ? "" : ",\"error\":6") + "}";
  }

  @ResponseBody
  @RequestMapping(value = "close", produces = "application/json", method = RequestMethod.POST)
  public String close(@RequestParam("concern_id") int concern_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    Concern concern = concernService.getById(concern_id);
    if (concern == null || concern.isClosed()) {
      return "{\"success\":false,\"error\":" + (concern == null ? 2 : 3) + "}";
    }

    if (!user.hasAuthority(CLOSE_AUTHORITY)) {
      logger.warn("User " + user.getUsername() + " tried to close Concern '" + concern.getName() + "' without Authority '" + CLOSE_AUTHORITY + "'");
      return "{\"success\":false,\"error\":5}";
    }

    logger.info("/data/concern/close[POST]: user " + user.getUsername() + " closed Concern #" + concern.getId());
    concern.setClosed(true);
    return "{\"success\":" + concernService.update(concern, user) + "}";
  }

  @ResponseBody
  @RequestMapping(value = "reopen", produces = "application/json", method = RequestMethod.POST)
  public String reopen(@RequestParam("concern_id") int concern_id, UsernamePasswordAuthenticationToken token) {
    Operator user = (Operator) token.getPrincipal();

    Concern concern = concernService.getById(concern_id);
    if (concern == null || !concern.isClosed()) {
      return "{\"success\":false,\"error\":" + (concern == null ? 2 : 4) + "}";
    }

    if (!user.hasAuthority(CLOSE_AUTHORITY)) {
      logger.warn("User " + user.getUsername() + " tried to reopen Concern '" + concern.getName() + "' without Authority '" + CLOSE_AUTHORITY + "'");
      return "{\"success\":false,\"error\":5}";
    }

    logger.info("/data/concern/reopen[POST]: user " + user.getUsername() + " reopened Concern #" + concern.getId());
    concern.setClosed(false);
    return "{\"success\":" + concernService.update(concern, user) + "}";
  }

}
