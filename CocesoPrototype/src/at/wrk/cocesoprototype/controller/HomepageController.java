package at.wrk.cocesoprototype.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import at.wrk.cocesoprototype.entities.Einheit;
import at.wrk.cocesoprototype.entities.Vorfall;
import at.wrk.cocesoprototype.service.EinheitService;
import at.wrk.cocesoprototype.service.VorfallService;

@Controller
public class HomepageController {
	
	@Autowired
	EinheitService einheitService;
	@Autowired
	VorfallService vorfallService;
	
	@RequestMapping("/register")
	public ModelAndView registerVorfall(@ModelAttribute Vorfall vorfall) {

		List<String> vorfallTypList = new ArrayList<String>();
		vorfallTypList.add("Einsatz");
		vorfallTypList.add("Auftrag");

		List<String> statusList = new ArrayList<String>();
		statusList.add("Neu");
		statusList.add("Offen");
		statusList.add("Disponiert");
		statusList.add("In Arbeit");
		statusList.add("Abgeschlossen");

		Map<String, List> map = new HashMap<String, List>();
		map.put("vorfallTypList", vorfallTypList);
		map.put("statusList", statusList);
		
		return new ModelAndView("register", "map", map);
	}
	
	@RequestMapping("/insert")
	public String insert(@ModelAttribute Vorfall vorfall) {
		
		if (vorfall != null)
			vorfallService.insert(vorfall);
		
		return "redirect:/getVorfallList";
	}
	
	@RequestMapping("/getVorfallList")
	public ModelAndView getVorfallandEinheitList() {
		
		List<Vorfall> vorfallList = vorfallService.getVorfallList();
		List<Einheit> einheitList = einheitService.getEinheitList();

		List<String> einheitStatusList = new ArrayList<String>();
		einheitStatusList.add("Zugewiesen");
		einheitStatusList.add("ZBO");
		einheitStatusList.add("BO");
		einheitStatusList.add("ZA");
		einheitStatusList.add("AO");
		einheitStatusList.add("Nicht mehr zugewiesen");

		Map<String, List> map = new HashMap<String, List>();
		map.put("vorfallList", vorfallList);
		map.put("einheitList", einheitList);
		map.put("einheitStatusList", einheitStatusList);
		
		return new ModelAndView("vorfallList", "map", map);
		
		//return new ModelAndView("vorfallList", "vorfallList", vorfallList);
	}
	
	@RequestMapping("/delete")
	public String deleteVorfall(@RequestParam int id) {
		
		System.out.println("vid = " + id);
		vorfallService.delete(id);
		
		return "redirect:/getVorfallList";
	}

}
