package at.wrk.coceso.gateway.restclient.impl;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.GET;

import at.wrk.coceso.gateway.restclient.RadioClient;
import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
class RadioClientImpl implements RadioClient {

    private final RestTemplate radioTemplate;

    @Autowired
    public RadioClientImpl(RestTemplate radioTemplate) {
        this.radioTemplate = requireNonNull(radioTemplate, "Radio RestTemplate must not be null");
    }

    @Override
    public void send(SendCallDto call) {
        radioTemplate.postForLocation("/calls", call);
    }

    @Override
    public List<ReceivedCallDto> getLast(int minutes) {
        return radioTemplate
                .exchange("/calls/last/{minutes}", GET, null, receivedListType(), minutes).getBody();
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
