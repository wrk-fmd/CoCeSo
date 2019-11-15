package at.wrk.coceso.radio.service.impl;

import at.wrk.coceso.radio.api.dto.Port;
import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.exception.IllegalMessageException;
import at.wrk.coceso.radio.exception.PortException;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.UnsupportedCommOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a physical Transceiver connected via serial port
 */
class Transceiver implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final byte STX = 0x02, ETX = 0x03, POISON = 0x00;
    private static final int ANI_LENGTH = 7; // fleet 3, id 4
    private static final Pattern ANI_PATTERN = Pattern.compile(String.format("^\\d{%d}$", ANI_LENGTH));
    private static final Pattern CALL_PATTERN = Pattern.compile(String.format("^I1(\\d{%d})$", ANI_LENGTH));
    private static final Pattern EMERGENCY_PATTERN = Pattern.compile(String.format("^E(\\d{%d})$", ANI_LENGTH));

    private final Consumer<ReceivedCallDto> listener;
    private final Port port;

    private final SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;

    private final BlockingQueue<Byte> queue = new LinkedBlockingQueue<>();
    private final AtomicInteger counter = new AtomicInteger();

    Transceiver(CommPortIdentifier identifier, String name, Consumer<ReceivedCallDto> listener) throws PortException {
        this.listener = Objects.requireNonNull(listener, "Radio call listener must not be null");
        this.port = new Port(identifier.getName(), name);

        LOG.debug("Trying to open serial port '{}'", identifier.getName());

        if (identifier.getPortType() != CommPortIdentifier.PORT_SERIAL) {
            throw new PortException(identifier.getName(), "Port is not a serial port");
        }

        try {
            // Open serial port
            serialPort = (SerialPort) identifier.open("Coceso radio plugin", 500);
        } catch (PortInUseException e) {
            close();
            throw new PortException(identifier.getName(), "Port is in use", e);
        }

        try {
            // Set serial port parameters
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {
            close();
            throw new PortException(identifier.getName(), "Failed to set serial port parameters", e);
        }

        try {
            // Retrieve output stream
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            close();
            throw new PortException(identifier.getName(), "Failed to access port output stream", e);
        }

        try {
            // Retrieve input stream
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            close();
            throw new PortException(identifier.getName(), "Failed to access port input stream", e);
        }

        try {
            // Attach listener to port
            serialPort.addEventListener(this::handleSerialPortEvent);
            serialPort.notifyOnDataAvailable(true);

            // Run thread handling the byte data
            new Thread(this::handleData).start();
        } catch (TooManyListenersException e) {
            close();
            throw new PortException(identifier.getName(), "Too many listeners on port", e);
        }

        LOG.info("Successfully opened port '{}'", identifier.getName());

        // Say hello to transceiver
        LOG.debug("Say hello to TRX");
        // TODO update to new hello message
        sendMessage("TE");
    }

    public Port getPort() {
        return port;
    }

    @Override
    public void close() {
        LOG.debug("Say goodbye to TRX");
        sendMessage("TD" + counter());

        LOG.debug("Sending poison pill to listener thread");
        queue.add(POISON);

        LOG.info("Closing serial port");
        serialPort.close();
    }

    /**
     * @return last two digits of {@link #counter} as String
     */
    private String counter() {
        return String.format("%02d", counter.updateAndGet(n -> (n + 1) % 100));
    }

    /**
     * Handler to read data from serial port.<br>
     * Gets triggered by Listener in librxtx and writes received data to {@link #queue}
     */
    private void handleSerialPortEvent(SerialPortEvent serialPortEvent) {
        LOG.debug("Received serial port event: {}", serialPortEvent);
        if (serialPortEvent.getEventType() != SerialPortEvent.DATA_AVAILABLE) {
            LOG.debug("Other event type: {}", serialPortEvent.getEventType());
            return;
        }

        try {
            LOG.debug("Reading from input stream ({} bytes available)", inputStream.available());
            while (inputStream.available() > 0) {
                queue.add((byte) inputStream.read());
            }
        } catch (IOException e) {
            LOG.error("Error reading from input stream", e);
        }
    }

    /**
     * Handler to extract messages from received byte stream.<br>
     * This should be run as a separate thread.
     */
    private void handleData() {
        StringBuilder message = null;
        while (true) {
            try {
                byte read = queue.take();
                if (read == POISON) {
                    // Poison pill, end loop
                    break;
                }

                if (read == STX) {
                    // Start of message
                    if (message != null) {
                        LOG.warn("{}: Previous message did not end, dropping '{}'", port.getName(), message.toString());
                    }

                    LOG.trace("{}: Starting new message", port.getName());
                    message = new StringBuilder();
                    continue;
                }

                if (message == null) {
                    LOG.warn("{}: Received data without message start, dropping '{}'", port.getName(), (char) read);
                    continue;
                }

                if (read == ETX) {
                    // End of message
                    LOG.trace("{}: Ending message", port.getName());

                    try {
                        handleMessage(message.toString());
                    } catch (Exception e) {
                        // We don't want exceptions in message handling to kill the loop under any circumstances
                        LOG.warn("Error handling received message", e);
                    }

                    message = null;
                    continue;
                }

                LOG.trace("{}: Appending '{}' to message", (char) read, port.getName());
                message.append((char) read);
            } catch (InterruptedException e) {
                LOG.warn("{}: Caught interrupt while waiting for data", port.getName());
            }
        }

        LOG.info("{}: Ended message loop", port.getName());
    }

    private void handleMessage(String message) {
        LOG.info("{}: Received message '{}'", port.getName(), message);

        Matcher callMatcher = CALL_PATTERN.matcher(message);
        if (callMatcher.matches()) {
            listener.accept(new ReceivedCallDto(port.getPath(), callMatcher.group(1), false));
            return;
        }

        Matcher emergencyMatcher = EMERGENCY_PATTERN.matcher(message);
        if (emergencyMatcher.matches()) {
            listener.accept(new ReceivedCallDto(port.getPath(), emergencyMatcher.group(1), true));
            return;
        }

        LOG.debug("{}: Unknown message type: '{}'", port.getName(), message);
    }

    public void sendCall(String ani) {
        LOG.info("{}: Sending call to {}", port.getName(), ani);

        if (!ANI_PATTERN.matcher(ani).matches()) {
            throw new IllegalMessageException();
        }

        // TODO Remove this completely or make it work with current radio programming
//        throw new UnsupportedOperationException();

        // Set ANI
        sendMessage("cK" + ani);

        // Send call
        sendMessage("pSelcall ");
    }

    private synchronized void sendMessage(String message) {
        try {
            LOG.debug("Sending message {} to port '{}'", message, port);

            outputStream.write(STX);
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.write(counter().getBytes(StandardCharsets.UTF_8));
            outputStream.write(ETX);
        } catch (IOException e) {
            LOG.error("Error sending data to port '{}'", port);
        }
    }
}
