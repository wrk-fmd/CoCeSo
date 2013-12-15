package at.wrk.coceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Robert on 15.12.13.
 *
 */
@Controller
@RequestMapping("/edit/person")
public class PersonController {
    public String index() {

        return "personmgmt.jsp";
    }
}
