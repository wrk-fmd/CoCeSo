package at.wrk.coceso.gateway.endpoint;

import static java.util.Objects.requireNonNull;

import at.wrk.coceso.gateway.restclient.RadioClient;
import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;
import at.wrk.coceso.radio.api.endpoint.RadioEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/radio")
class RadioEndpointProxy implements RadioEndpoint {

    private final RadioClient radioClient;

    @Autowired
    public RadioEndpointProxy(RadioClient radioClient) {
        this.radioClient = requireNonNull(radioClient, "RadioClient must not be null");
    }

    @Override
    public void send(SendCallDto call) {
        radioClient.send(call);
    }

    @Override
    public List<ReceivedCallDto> getLast(int minutes) {
        return radioClient.getLast(minutes);
    }

    @Override
    public List<Port> ports() {
        return radioClient.ports();
    }

    @Override
    public void reloadPorts() {
        radioClient.reloadPorts();
    }
}
