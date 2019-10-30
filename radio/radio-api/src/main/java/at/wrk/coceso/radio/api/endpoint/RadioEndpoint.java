package at.wrk.coceso.radio.api.endpoint;

import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;
import at.wrk.coceso.radio.api.exception.UnknownPortException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RequestMapping("/")
public interface RadioEndpoint {

    @RequestMapping(value = "/calls", method = RequestMethod.POST)
    void send(@RequestBody SendCallDto call) throws UnknownPortException;

    @RequestMapping(value = "/calls/last/{minutes}", method = RequestMethod.GET)
    List<ReceivedCallDto> getLast(@PathVariable("minutes") int minutes);

    @RequestMapping(value = "/ports", method = RequestMethod.GET)
    List<Port> ports();

    @RequestMapping(value = "/ports/reload", method = RequestMethod.POST)
    void reloadPorts();
}
