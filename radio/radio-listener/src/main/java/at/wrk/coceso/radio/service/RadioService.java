package at.wrk.coceso.radio.service;

import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;
import at.wrk.coceso.radio.api.exception.UnknownPortException;

import java.time.temporal.TemporalAmount;
import java.util.List;

public interface RadioService {

    List<ReceivedCallDto> getLast(TemporalAmount timespan);

    void sendCall(SendCallDto call) throws UnknownPortException;

    List<Port> getPorts();

    void reloadPorts();
}
