package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.Selcall;
import at.wrk.coceso.service.SelcallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

/**
 * Created by Robert on 14.06.2014.
 */
@Controller
@RequestMapping("/data/selcall")
public class SelcallController {

    @Autowired
    SelcallService selcallService;

    @RequestMapping(value = "receive", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public DeferredResult<Selcall> receive() {
        DeferredResult<Selcall> result = new DeferredResult<Selcall>();

        selcallService.receiveRequest(result);

        return result;
    }

    @RequestMapping(value = "send", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String send(@RequestBody Selcall selcall) {

        return String.format("{\"sucess\":%b}", selcallService.sendSelcall(selcall));
    }

    @RequestMapping(value = "getLastHour", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Selcall> getLastHour() {
        return selcallService.getLastHour();
    }
}
