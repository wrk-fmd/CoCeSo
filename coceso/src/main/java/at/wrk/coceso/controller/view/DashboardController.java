package at.wrk.coceso.controller.view;

import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.service.ConcernService;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.LogService;
import at.wrk.coceso.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Objects;
import javassist.NotFoundException;
import javax.servlet.http.HttpServletResponse;

@Controller
public class DashboardController {

  @Autowired
  private ConcernService concernService;

  @Autowired
  private LogService logService;

  @Autowired
  private UnitService unitService;

  @Autowired
  private IncidentService incidentService;

  @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
  public String showDashboard(ModelMap map, HttpServletResponse response,
          @RequestParam(value = "view", defaultValue = "") String view,
          @RequestParam(value = "uid", required = false) Integer uid,
          @RequestParam(value = "iid", required = false) Integer iid,
          @RequestParam(value = "active", defaultValue = "0") boolean active,
          @RequestParam(value = "concern", required = false) Integer concern_id,
          @CookieValue(value = "concern", required = false) Integer cookie_id) throws IOException {
    map.addAttribute("concerns", concernService.getAll());

    try {
      if (iid != null && uid != null) {
        crossDetail(map, iid, uid);
      } else if (iid != null) {
        incidentDetail(map, iid);
      } else if (uid != null) {
        unitDetail(map, uid);
      } else {
        if (concern_id == null) {
          concern_id = cookie_id != null ? cookie_id : 0;
        }
        map.addAttribute("concern", concern_id);

        switch (view) {
          case "unit":
            this.unitList(map, concern_id);
            break;
          case "incident":
            this.incidentList(map, concern_id, active);
            break;
          default:
            this.logList(map, concern_id);
            break;
        }
      }
    } catch (NotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    return "dashboard";
  }

  private void logList(ModelMap map, Integer concern_id) {
    map.addAttribute("template", "log_table");
    map.addAttribute("log_menu", "active");
    map.addAttribute("logs", logService.getAll(concern_id));
  }

  private void incidentList(ModelMap map, int concern_id, boolean active) {
    map.addAttribute("template", "incident_list");
    map.addAttribute("incident_menu", "active");
    map.addAttribute("incidents", active ? incidentService.getAllActive(concern_id) : incidentService.getAll(concern_id));
  }

  private void unitList(ModelMap map, int concern_id) {
    map.addAttribute("template", "unit_list");
    map.addAttribute("unit_menu", "active");
    map.addAttribute("units", unitService.getAll(concern_id));
  }

  private void crossDetail(ModelMap map, int incident_id, int unit_id) throws NotFoundException {
    Incident incident = incidentService.getById(incident_id);
    Unit unit = unitService.getById(unit_id);
    if (unit == null || incident == null || !Objects.equals(incident.getConcern(), unit.getConcern())) {
      throw new NotFoundException("");
    }

    map.addAttribute("template", "cross_detail");
    map.addAttribute("concern", incident.getConcern());
    map.addAttribute("incident", incident);
    map.addAttribute("unit", unit);
    map.addAttribute("logs", logService.getByIncidentAndUnit(incident_id, unit_id));
  }

  private void incidentDetail(ModelMap map, int incident_id) throws NotFoundException {
    Incident incident = incidentService.getById(incident_id);
    if (incident == null) {
      throw new NotFoundException("");
    }

    map.addAttribute("template", "incident_detail");
    map.addAttribute("incident_menu", "active");
    map.addAttribute("concern", incident.getConcern());
    map.addAttribute("incident", incident);
    map.addAttribute("units", unitService.getRelated(incident_id));
    map.addAttribute("logs", logService.getByIncidentId(incident_id));
  }

  private void unitDetail(ModelMap map, int unit_id) throws NotFoundException {
    Unit unit = unitService.getById(unit_id);
    if (unit == null) {
      throw new NotFoundException("");
    }

    map.addAttribute("template", "unit_detail");
    map.addAttribute("unit_menu", "active");
    map.addAttribute("concern", unit.getConcern());
    map.addAttribute("unit", unit);
    map.addAttribute("incidents", incidentService.getRelated(unit_id));
    map.addAttribute("logs", logService.getByUnitId(unit_id));
  }

}
