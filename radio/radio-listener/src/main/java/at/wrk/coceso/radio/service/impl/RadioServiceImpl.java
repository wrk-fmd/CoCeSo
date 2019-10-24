package at.wrk.coceso.radio.service.impl;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;
import at.wrk.coceso.radio.api.exception.UnknownPortException;
import at.wrk.coceso.radio.api.queues.RadioQueueNames;
import at.wrk.coceso.radio.entity.RadioCall;
import at.wrk.coceso.radio.entity.RadioCall.Direction;
import at.wrk.coceso.radio.exception.PortException;
import at.wrk.coceso.radio.mapper.RadioCallMapper;
import at.wrk.coceso.radio.repository.RadioCallRepository;
import at.wrk.coceso.radio.service.RadioService;
import gnu.io.CommPortIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:ports.properties", ignoreResourceNotFound = true)
public class RadioServiceImpl implements RadioService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Environment properties;
    private final RadioCallRepository repository;
    private final RadioCallMapper mapper;
    private final AmqpTemplate amqp;

    private final ConcurrentMap<String, Transceiver> transceivers;

    private List<Port> ports;

    @Autowired
    public RadioServiceImpl(Environment properties, RadioCallRepository repository, RadioCallMapper mapper,
            AmqpTemplate amqp) {
        this.properties = Objects.requireNonNull(properties, "Properties must not be null");
        this.repository = Objects.requireNonNull(repository, "RadioCallRepository must not be null");
        this.mapper = Objects.requireNonNull(mapper, "RadioCallMapper must not be null");
        this.amqp = Objects.requireNonNull(amqp, "AmqpTemplate must not be null");

        transceivers = new ConcurrentHashMap<>();
        reloadPorts();
    }

    private void receiveCall(ReceivedCallDto call) {
        LOG.info("Call received from '{}'", call.getAni());
        repository.save(mapper.receivedCallToRadioCall(call));
        this.amqp.convertAndSend(RadioQueueNames.CALLS_RECEIVED, null, call);
    }

    public List<ReceivedCallDto> getLast(TemporalAmount timespan) {
        return mapper.radioCallToReceivedCall(
                repository.findReceivedAfter(Instant.now().minus(timespan), EnumSet.of(Direction.RX, Direction.RX_EMG))
        );
    }

    @Override
    public void sendCall(SendCallDto call) throws UnknownPortException {
        if (call == null || call.getAni() == null) {
            LOG.info("Call object or ANI is null");
            throw new IllegalArgumentException("Call object or ANI is null");
        }

        String port = call.getPort(), ani = call.getAni();
        if (port == null) {
            LOG.debug("Trying to send RadioCall to '{}' on all ports", ani);
            transceivers.values().forEach(transceiver -> transceiver.sendCall(ani));
        } else {
            LOG.debug("Trying to send RadioCall to '{}' on port '{}'", ani, port);
            Transceiver transceiver = transceivers.get(port);
            if (transceiver == null) {
                LOG.warn("Tried to send call for '{}' to non-existing port '{}'", ani, port);
                throw new UnknownPortException("Port does not exist");
            }
            transceiver.sendCall(ani);
        }

        RadioCall radioCall = mapper.sendCallToRadioCall(call);
        radioCall.setTimestamp(Instant.now());
        radioCall.setDirection(Direction.TX);
        repository.save(radioCall);
    }

    public List<Port> getPorts() {
        if (ports == null) {
            ports = transceivers.values().stream().map(Transceiver::getPort).collect(Collectors.toList());
        }
        return ports;
    }

    public final synchronized void reloadPorts() {
        shutdown();

        LOG.info("(Re)detecting ports...");

        @SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    String name = portIdentifier.getName();
                    Transceiver t = new Transceiver(portIdentifier, properties.getProperty(name), this::receiveCall);
                    transceivers.put(name, t);
                } catch (PortException e) {
                    LOG.error("Initialization of port '{}' failed", portIdentifier.getName(), e);
                    // Port in use
                    // intended NOP
                }
            }
        }
    }

    private synchronized void shutdown() {
        LOG.info("Closing all ports...");
        transceivers.values().forEach(Transceiver::close);
        transceivers.clear();
        ports = null;
    }
}
