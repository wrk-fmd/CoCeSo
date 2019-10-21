package at.wrk.coceso.gateway.endpoint;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.GET;

import java.util.List;

import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;
import at.wrk.coceso.radio.api.endpoint.RadioEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/radio")
class RadioEndpointProxy implements RadioEndpoint {

    private final RestTemplate radioTemplate;

    @Autowired
    public RadioEndpointProxy(RestTemplate radioTemplate) {
        this.radioTemplate = requireNonNull(radioTemplate, "Radio RestTemplate must not be null");
    }

    @Override
    public void send(SendCallDto call) {
        radioTemplate.postForLocation("/calls", call);
    }

    @Override
    public List<ReceivedCallDto> getLast(int minutes) {
        return radioTemplate.exchange("/calls/last/{minutes}", GET, null, receivedListType(), minutes).getBody();
    }

    private ParameterizedTypeReference<List<ReceivedCallDto>> receivedListType() {
        return new ParameterizedTypeReference<>() {
        };
    }

    @Override
    public List<Port> ports() {
        return radioTemplate.exchange("/ports", GET, null, portsListType()).getBody();
    }

    private ParameterizedTypeReference<List<Port>> portsListType() {
        return new ParameterizedTypeReference<>() {
        };
    }

    @Override
    public void reloadPorts() {
        radioTemplate.postForLocation("/ports/reload", null);
    }
}
