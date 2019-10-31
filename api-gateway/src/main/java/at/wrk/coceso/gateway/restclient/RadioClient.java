package at.wrk.coceso.gateway.restclient;

import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;

import java.util.List;

public interface RadioClient {

    void send(SendCallDto call);

    List<ReceivedCallDto> getLast(int minutes);

    List<Port> ports();

    void reloadPorts();
}
