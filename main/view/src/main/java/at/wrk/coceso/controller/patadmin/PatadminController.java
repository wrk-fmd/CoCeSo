package at.wrk.coceso.controller.patadmin;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.User;
import at.wrk.coceso.form.GroupsForm;
import at.wrk.coceso.service.patadmin.PatadminService;
import at.wrk.coceso.service.patadmin.PatadminWriteService;
import at.wrk.coceso.utils.ActiveConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Controller
@RequestMapping(value = "/patadmin")
public class PatadminController {

  private final static Logger LOG = LoggerFactory.getLogger(PatadminController.class);

  @Autowired
  private PatadminService patadminService;

  @Autowired
  private PatadminWriteService patadminWriteService;

  @PreAuthorize("@auth.hasPermission(#concern, 'Patadmin')")
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String showIndex(final ModelMap map, @ActiveConcern final Concern concern) {
    boolean[] accessLevels = patadminService.getAccessLevels(concern);

    if (!accessLevels[0]) {
      if (accessLevels[1] && !accessLevels[2] && !accessLevels[3]) {
        return "redirect:/patadmin/registration";
      }
      if (!accessLevels[1] && accessLevels[2] && !accessLevels[3]) {
        return "redirect:/patadmin/postprocessing";
      }
      if (!accessLevels[1] && !accessLevels[2] && accessLevels[3]) {
        return "redirect:/patadmin/info";
      }
    }

    map.addAttribute("accessLevels", accessLevels);
    map.addAttribute("concern", concern);
    return "patadmin/index";
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminSettings')")
  @RequestMapping(value = "/settings", method = RequestMethod.GET)
  public ModelAndView showSettings(final ModelMap map, @ActiveConcern final Concern concern) {
    try {
      File[] images = new ClassPathResource("../../static/imgs/groups").getFile().listFiles();
      Arrays.sort(images);
      map.addAttribute("images", images);
    } catch (IOException ex) {
      LOG.error("Error loading group image files", ex);
    }

    patadminService.addAccessLevels(map, concern);
    GroupsForm form = new GroupsForm(patadminService.getGroups(concern));
    return new ModelAndView("patadmin/settings", "form", form);
  }

  @PreAuthorize("@auth.hasPermission(#concern, 'PatadminSettings')")
  @RequestMapping(value = "/settings", method = RequestMethod.POST)
  public String saveSettings(@ModelAttribute final GroupsForm form, @ActiveConcern final Concern concern, @AuthenticationPrincipal final User user) {
    patadminWriteService.update(form, concern, user);
    return "redirect:settings";
  }

}
