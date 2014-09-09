package at.wrk.coceso.controller.data;

import at.wrk.coceso.dao.UnitContainerDao;
import at.wrk.coceso.entity.helper.SlimUnit;
import at.wrk.coceso.entity.helper.SlimUnitContainer;
import at.wrk.coceso.entity.helper.UnitContainer;
import at.wrk.coceso.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/data/unitContainer")
public class UnitContainerController {

  @Autowired
  private UnitContainerDao containerDao;

  @Autowired
  private ContainerService containerService;

  /**
   * Full Hierarchy
   *
   * @param concern_id Cookie with active Concern
   * @return Top Container with full Hierarchy and spare Units in top.units
   */
  @RequestMapping(value = "getWithSpare", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public UnitContainer getWithSpare(@CookieValue("concern") int concern_id) {
    UnitContainer top = containerDao.getByConcernId(concern_id);
    top.getUnits().addAll(containerDao.getSpareUnits(concern_id));
    return top;
  }

  /**
   * Full Hierarchy
   *
   * @param concern_id Cookie with active Concern
   * @return Top Container with full Hierarchy, WITHOUT Spare Units
   */
  @RequestMapping(value = "get", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public UnitContainer get(@CookieValue("concern") int concern_id) {
    return containerDao.getByConcernId(concern_id);
  }

  /**
   * Full Hierarchy,
   *
   * @param concern_id Cookie with active Concern
   * @return Top Slim Container with full Hierarchy, WITH Spare Units
   */
  @RequestMapping(value = "getSlim", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public SlimUnitContainer getSlim(@CookieValue("concern") int concern_id) {
    return new SlimUnitContainer(getWithSpare(concern_id));
  }

  /**
   * No Hierarchy, only plain Container, spare Units NOT included
   *
   * @param concern_id Cookie with active Concern
   * @return all Containers of Concern
   */
  @RequestMapping(value = "getList", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public List<UnitContainer> getList(@CookieValue("concern") int concern_id) {
    return containerDao.getByConcernSlim(concern_id);
  }

  @RequestMapping(value = "getSpare", produces = "application/json")
  @ResponseBody
  public List<SlimUnit> getSpare(@CookieValue("concern") int concern_id) {
    return containerDao.getSpareUnits(concern_id);
  }

  /**
   * id == -1 for new Container, ordering == -2 for Deletion
   *
   * @param container Container to update
   * @return Status JSON
   */
  @RequestMapping(value = "updateContainer", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public String updateContainer(@RequestBody UnitContainer container) {
    int ret = containerService.update(container);
    if (ret == -1) {
      return "{\"success\": false, \"info\":\"Something went wrong on update\"}";
    }
    return "{\"success\": true, \"id\":" + ret + "}";
  }

  @RequestMapping(value = "updateUnit", produces = "application/json", method = RequestMethod.POST)
  @ResponseBody
  public String updateUnit(@RequestParam("container_id") int containerId,
          @RequestParam("unit_id") int unitId, @RequestParam("ordering") double ordering) {
    if (!containerService.updateUnit(containerId, unitId, ordering)) {
      return "{\"success\": false, \"info\":\"Something went wrong on update\"}";
    }
    return "{\"success\": true}";
  }

}
