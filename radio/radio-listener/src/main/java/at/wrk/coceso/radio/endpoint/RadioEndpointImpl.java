package at.wrk.coceso.radio.endpoint;

import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;
import at.wrk.coceso.radio.api.endpoint.RadioEndpoint;
import at.wrk.coceso.radio.api.exception.UnknownPortException;
import at.wrk.coceso.radio.service.RadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
public class RadioEndpointImpl implements RadioEndpoint {

    private final RadioService radioService;

    @Autowired
    public RadioEndpointImpl(RadioService radioService) {
        this.radioService = Objects.requireNonNull(radioService, "RadioService must not be null");
    }

    @Override
    public void send(@RequestBody @Valid SendCallDto call) throws UnknownPortException {
        radioService.sendCall(call);
    }

    @Override
    public List<ReceivedCallDto> getLast(@PathVariable int minutes) {
        return radioService.getLast(Duration.ofMinutes(minutes));
    }

    @Override
    public List<Port> ports() {
        return radioService.getPorts();
    }

    @Override
    public void reloadPorts() {
        radioService.reloadPorts();
    }
}
