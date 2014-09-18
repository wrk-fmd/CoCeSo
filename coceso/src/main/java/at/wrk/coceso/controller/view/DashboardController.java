package at.wrk.coceso.controller.view;

import at.wrk.coceso.dao.ConcernDao;
import at.wrk.coceso.dao.LogDao;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Unit;
import at.wrk.coceso.entity.enums.TaskState;
import at.wrk.coceso.service.IncidentService;
import at.wrk.coceso.service.TaskService;
import at.wrk.coceso.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DashboardController {

    // TODO Change to Service
    @Autowired
    ConcernDao concernDao;

    @Autowired
    LogDao logDao;

    @Autowired
    UnitService unitService;

    @Autowired
    IncidentService incidentService;

    @Autowired
    TaskService taskService;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String showDashboard(@RequestParam(value = "sub", defaultValue = "Log") String s_sub,
                                @RequestParam(value = "uid", required = false) Integer uid,
                                @RequestParam(value = "iid", required = false) Integer iid,
                                ModelMap map,
                                @RequestParam(value = "concern", required = false) Integer concern_id,
                                @CookieValue(value = "concern", required = false) Integer cookie_id)
    {
      if (concern_id == null) {
        concern_id = cookie_id != null ? cookie_id : 0;
      }

        map.addAttribute("concern", concern_id);

        map.addAttribute("concerns", concernDao.getAll());

        if(uid != null)
            map.addAttribute("uid", uid);
        if(iid != null)
            map.addAttribute("iid", iid);
        map.addAttribute("sub", s_sub);

        if(s_sub.equals("Unit")) {
            map.addAttribute("unit", "active");
            map.addAttribute("sel_units", unitService.getAll(concern_id));

            if(uid != null && uid > 0) {
                Unit ret = unitService.getById(uid);
                if(ret == null) {
                    map.addAttribute("error", "No Unit found");
                }
                else {
                    map.addAttribute("u_unit", ret);

                }
            }
            else {
                map.addAttribute("units", unitService.getAll(concern_id));
            }

        } else if(s_sub.equals("Incident")) {
            map.addAttribute("incident", "active");

            if(uid != null && uid == -1) {
                map.addAttribute("incidents", incidentService.getAllActive(concern_id));
            } else if(iid != null) {
                Incident ret = incidentService.getById(iid);

                if(ret == null) {
                    map.addAttribute("error", "No Incident Found");
                }
                else {
                    Map<Integer, String> i_map = new HashMap<>();
                    for(Map.Entry<Integer, TaskState> entry : ret.getUnits().entrySet()) {
                        Unit u = unitService.getById(entry.getKey());
                        i_map.put(u.getId(), u.getCall());
                    }
                    map.addAttribute("i_map", i_map);

                    map.addAttribute("i_incident", ret);
                }
            } else {
                map.addAttribute("incidents", incidentService.getAll(concern_id));
            }


        } else if(s_sub.equals("Task")) {
            map.addAttribute("task", "active");
            if((uid == null && iid == null) || (uid != null && iid != null)) {
                map.addAttribute("error", "No ID specified");
            }
            else if(uid != null) {
                map.addAttribute("tasks", taskService.getAllByUnitId(uid));
            }
            else {
                map.addAttribute("tasks", taskService.getAllByIncidentId(iid));
            }

        } else {
            map.addAttribute("log", "active");
            if((uid == null && iid == null) || (uid != null && iid != null)) {
                map.addAttribute("logs", logDao.getAll(concern_id));
            }
            else if(uid != null) {
                map.addAttribute("logs", logDao.getByUnitId(uid));
            }
            else {
                map.addAttribute("logs", logDao.getByIncidentId(iid));
            }
        }


        return "dashboard";
    }

}
