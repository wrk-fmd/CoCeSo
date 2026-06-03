package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.utils.ActiveConcern;
import at.wrk.coceso.utils.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/patadmin/transport", method = RequestMethod.GET)
public class TransportOverviewController {

  @Autowired
  private PatadminService patadminService;

  @ModelAttribute("viewType")
  public String viewType() {
    return "transport";
  }

  @ModelAttribute("showSearch")
  public boolean showSearch() {
    return false;
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminTransport')")
  @Transactional
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String showHome(
          final ModelMap map,
          @ActiveConcern final Concern concern) {
    map.addAttribute("patients", Initializer.initGroups(patadminService.getAllInTransport(concern)));
    patadminService.addAccessLevels(map, concern);
    return "patadmin/transport/list";
  }

}
