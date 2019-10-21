package at.wrk.coceso.radio.service;

import java.util.List;

import at.wrk.coceso.radio.entity.Port;
import at.wrk.coceso.radio.entity.RadioCall;

public interface RadioService {

    List<RadioCall> getLastMinutes(int minutes);

    boolean sendCall(RadioCall selcall);

    List<Port> getPorts();

    boolean reloadPorts();
}
