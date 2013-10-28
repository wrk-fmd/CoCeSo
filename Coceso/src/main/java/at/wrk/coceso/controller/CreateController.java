package at.wrk.coceso.controller;

import at.wrk.coceso.dao.CaseDao;
import at.wrk.coceso.dao.UnitDao;
import at.wrk.coceso.entities.Case;
import at.wrk.coceso.entities.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class CreateController {

    @Autowired
    CaseDao caseDao;

    @Autowired
    UnitDao unitDao;

    @RequestMapping("/create")
    public String create(int id) {

        return "redirect:/edit/"+id;
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, ModelMap map) {

        Case caze = caseDao.getById(id);
        List<Unit> unit_list = unitDao.getAll(id);

        map.addAttribute("caze", caze);
        map.addAttribute("unit_list", unit_list);

        return "edit";
    }

}
