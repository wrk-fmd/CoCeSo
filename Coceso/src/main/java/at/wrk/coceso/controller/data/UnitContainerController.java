package at.wrk.coceso.controller.data;

import at.wrk.coceso.dao.UnitContainerDao;
import at.wrk.coceso.entity.helper.SlimUnit;
import at.wrk.coceso.entity.helper.UnitContainer;
import at.wrk.coceso.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/data/unitContainer")
public class UnitContainerController {
    @Autowired
    UnitContainerDao containerDao;

    @Autowired
    ContainerService containerService;

    /**
     * Full Hierarchy
     * @param concernId Cookie with active Concern
     * @return Top Container with full Hierarchy and spare Units in top.units
     */
    @RequestMapping(value = "getWithSpare", produces = "application/json")
    @ResponseBody
    public UnitContainer getWithSpare(@CookieValue(value = "active_case", defaultValue = "0") String concernId){
        try {
            UnitContainer top = containerDao.getByConcernId(Integer.parseInt(concernId));
            top.getUnits().addAll(containerDao.getSpareUnits(Integer.parseInt(concernId)));
            return top;
        } catch(NumberFormatException e) {
            return null;
        }
    }

    /**
     * Full Hierarchy
     * @param concernId Cookie with active Concern
     * @return Top Container with full Hierarchy, WITHOUT Spare Units
     */
    @RequestMapping(value = "get", produces = "application/json")
    @ResponseBody
    public UnitContainer get(@CookieValue(value = "active_case", defaultValue = "0") String concernId){
        try {
            return containerDao.getByConcernId(Integer.parseInt(concernId));
        } catch(NumberFormatException e) {
            return null;
        }
    }


    /**
     * No Hierarchy, only plain Container, spare Units NOT included
     * @param concernId Cookie with active Concern
     * @return all Containers of Concern
     */
    @RequestMapping(value = "getList", produces = "application/json")
    @ResponseBody
    public List<UnitContainer> getList(@CookieValue(value = "active_case", defaultValue = "0") String concernId){
        try {
            return containerDao.getByConcernSlim(Integer.parseInt(concernId));
        } catch(NumberFormatException e) {
            return new ArrayList<UnitContainer>();
        }
    }

    @RequestMapping(value = "getSpare", produces = "application/json")
    @ResponseBody
    public List<SlimUnit> getSpare(@CookieValue(value = "active_case", defaultValue = "0") String concernId){
        try {
            return containerDao.getSpareUnits(Integer.parseInt(concernId));
        } catch(NumberFormatException e) {
            return new ArrayList<SlimUnit>();
        }
    }

    /**
     * id <= 0 for new Container, ordering == -2 for Deletion
     * @param container Container to update
     * @return Status JSON
     */
    @RequestMapping(value = "updateContainer", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateContainer(@RequestBody UnitContainer container){

        if(containerService.update(container) == -1) {
            return new ResponseEntity<String>("{\"success\": false, \"info\":\"Something went wrong on update\"}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("{\"success\": true}", HttpStatus.OK);
    }

    @RequestMapping(value = "updateUnit/{containerId}/{unitId}/{ordering}", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public String updateUnit(@PathVariable("containerId") int containerId,
                             @PathVariable("unitId") int unitId, @PathVariable("ordering") double ordering){

        if(!containerService.updateUnit(containerId, unitId, ordering)) {
            return "{\"success\": false, \"info\":\"Something went wrong on update\"}";
        }
        return "{\"success\": true}";
    }

}
