package at.wrk.selcall;

import gnu.io.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Robert on 12.06.2014.
 *
 * RXTX from http://create-lab-commons.googlecode.com/svn/trunk/java/lib/rxtx/
 */
public class TransceiverManagerImpl extends AbstractTransceiverManager {

    private static TransceiverManagerImpl instance = null;
    private final HashMap<String, Transceiver> transceivers;

    private final static Logger LOG = Logger.getLogger(TransceiverManagerImpl.class);

    private TransceiverManagerImpl() {
        transceivers = new HashMap<>();

        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                String port = portIdentifier.getName();
                try {
                    Transceiver t_trx = new Transceiver(port, this);
                    transceivers.put(port, t_trx);
                } catch (IllegalArgumentException e) {
                    // Port in use
                    // intended NOP
                }
            }
        }
    }

    public static synchronized TransceiverManagerImpl getInstance() {
        if(instance == null) {
            instance = new TransceiverManagerImpl();
        }
        return instance;
    }

    /*@PreDestroy
    protected void destroy() {
        for (Iterator<Transceiver> it = transceivers.values().iterator(); it.hasNext();) {
            it.next().closeSerialPort();
            it.remove();
        }
    }*/

    @Override
    public Set<String> getPorts() {
        return transceivers.keySet();
    }

    @Override
    public void send(String port, String message) {
        if (!transceivers.containsKey(port)) {
            LOG.warn(String.format("Tried to send message '%s' to non-existing port '%s'", message, port));
            throw new IllegalArgumentException("Port does not exist!");
        }
        transceivers.get(port).sendMessage(message);
    }

    /**
     * ########################################################### Represents a
     * physical Transceiver connected via serial port
     * ###########################################################
     */
    class Transceiver {

        private final Logger LOG = Logger.getLogger(Transceiver.class);

        private final String port;
        private String friendlyName;

        private static final String sendCommand = "Selcall";

        private static final int MESSAGE_LENGTH = 7; // fleet 3, id 4

        private SerialPort serialPort;
        private InputStream inputStream;
        private OutputStream outputStream;
        private boolean portReady = false;

        private ConcurrentLinkedQueue<Byte> queue;

        private int counter = 0;

        private ReceivedMessageListener receivedMessageListener;

        Transceiver(String port, ReceivedMessageListener listener) {

            if (port == null) {
                throw new IllegalArgumentException("Port must not be null!");
            }

            this.port = port;
            this.friendlyName = "";

            this.receivedMessageListener = listener;

            queue = new ConcurrentLinkedQueue<>();

            if (!openSerialPort()) {
                // error
                throw new IllegalArgumentException(String.format("Failed to open port '%s'", port));
            }

            helloToTransceiver();
        }

        Transceiver(String port, String friendlyName, ReceivedMessageListener listener) {
            this(port, listener);
            setFriendlyName(friendlyName);
        }

        /**
         *
         * @return true on success, false on error
         */
        private boolean openSerialPort() {
            CommPortIdentifier identifier;
            LOG.debug(String.format("try to open serial port '%s'", port));

            try {
                identifier = CommPortIdentifier.getPortIdentifier(port);
                if (identifier.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                    LOG.error("Port is no serial port!");
                    return false;
                }
            } catch (NoSuchPortException e) {
                LOG.warn(String.format("No Such Port: %s", port));
                return false;
            }

            try {
                serialPort = (SerialPort) identifier.open("CoCeSo Selcall by robow", 500);
            } catch (PortInUseException e) {
                LOG.warn(String.format("Port '%s' in use", port), e);
                return false;
            }

            try {
                serialPort.setSerialPortParams(
                        9600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_2,
                        SerialPort.PARITY_NONE);
            } catch (UnsupportedCommOperationException e) {
                LOG.error("failed to set serial port parameters", e);
                return false;
            }

            try {
                outputStream = serialPort.getOutputStream();
            } catch (IOException e) {
                LOG.error("No access to OutputStream", e);
                return false;
            }

            try {
                inputStream = serialPort.getInputStream();
            } catch (IOException e) {
                LOG.error("No access to InputStream", e);
                return false;
            }

            try {
                serialPort.addEventListener(new SerialPortEventListener() {
                    @Override
                    public void serialEvent(SerialPortEvent serialPortEvent) {
                        LOG.debug("Serial port event");

                        switch (serialPortEvent.getEventType()) {
                            case SerialPortEvent.DATA_AVAILABLE:
                                LOG.debug("Data available. Trigger #readData()");
                                readData();
                                break;
                            default:
                                LOG.debug(String.format("Other Event: %d", serialPortEvent.getEventType()));
                                break;
                        }
                    }
                });
                serialPort.notifyOnDataAvailable(true);
            } catch (TooManyListenersException e) {
                LOG.error("Too many Listeners");
                return false;
            }

            LOG.info("Succesfully opened port");
            return this.portReady = true;
        }

        private void closeSerialPort() {
            if (portReady) {
                LOG.debug("Say goodbye to TRX");
                try {
                    sendMessage("TD" + counter());
                } catch (IllegalMessageException e) {
                    LOG.error("this should not happen. internal error at goodbye-message", e);
                }
            }

            portReady = false;

            LOG.info("Close Serial Port");
            serialPort.close();
        }

        private void helloToTransceiver() {
            if (portReady) {
                LOG.debug("Say hello to TRX");
                try {
                    // TODO update to new hello message
                    //sendMessage("TE");
                } catch (IllegalMessageException e) {
                    LOG.error("this should not happen. internal error at hello-message", e);
                }
            }
        }

        /**
         *
         * @return last two digits of <code>counter</code> as String
         */
        private String counter() {
            this.counter %= 100;
            return (++this.counter < 10 ? "0" : "") + this.counter;
        }

        /**
         * Handler to read data from serial port. Gets triggered by Listener in
         * librxtx Writes received data to <code>queue</code>
         */
        private synchronized void readData() {
            if (!portReady) {
                LOG.warn("Port not ready!");
                return;
            }

            byte[] data = new byte[128];
            int i;
            LOG.info("100ms delay...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOG.info("hey! don't wake me!", e);
            }
            LOG.info("try to read from inputStream");
            try {
                while (inputStream.available() > 0) {
                    LOG.debug("Read block");
                    i = inputStream.read(data);
                    for (int j = 0; j < i; j++) {
                        queue.add(data[j]);
                    }
                }
            } catch (IOException e) {
                LOG.error(e);
            }
            processData();
        }

        private synchronized void processData() {
            LOG.info("processing Data...");
            LOG.debug("queue: " + Arrays.toString(queue.toArray()));

            byte etx = 0x03;
            while (queue.contains(etx)) { // check if the End-Of-Transmission Byte is in the queue

                StringBuilder message = new StringBuilder();

                byte tmp = 0x00;
                while (!queue.isEmpty() && (tmp = queue.poll()) != 0x02) {
                    LOG.debug("dump byte with value 0x" + tmp);
                }
                if (queue.isEmpty()) {
                    LOG.info("queue empty! aborting.");
                    return;
                }
                while (!queue.isEmpty() && (tmp = queue.poll()) != 0x03) {
                    // write character to message string
                    message.append((char) tmp);
                }
                if (queue.isEmpty() && tmp != 0x03) {
                    LOG.error("reached end of queue without 0x03 end byte"); // Should not be possible -> corrupt data
                    return;
                }

                if (message.substring(0, 2).equals("I1") && message.length() >= MESSAGE_LENGTH + 2) {
                    LOG.info(String.format("Decoded Message: '%s'", message));
                    receivedMessageListener.handleMessage(port, message.substring(2, MESSAGE_LENGTH + 2));
                } else if (message.substring(0, 1).equals("E") && message.length() >= MESSAGE_LENGTH + 1) {
                    LOG.info(String.format("Decoded Message: '%s'", message));
                    receivedMessageListener.handleEmergency(port, message.substring(1, MESSAGE_LENGTH + 1));
                } else {
                    LOG.debug(String.format("unknown message (length: %d): '%s'", message.length(), message));
                }
            }
        }

        public void setReceivedMessageListener(ReceivedMessageListener listener) {
            this.receivedMessageListener = listener;
        }

        public synchronized boolean sendMessage(String message) throws IllegalMessageException {
            LOG.info(String.format("Try to send message '%s'", message));

            if (!portReady) {
                LOG.warn("Port not ready!");
                return false;
            }

            if (message == null) {
                throw new IllegalMessageException();
            }

            if (message.equals("TE") || message.equals("TD")) {
                byte[] send = {0x02};
                send = ArrayUtils.addAll(send, message.getBytes());
                send = ArrayUtils.addAll(send, counter().getBytes());
                send = ArrayUtils.add(send, (byte) 0x03);

                try {
                    outputStream.write(send);
                } catch (IOException e) {
                    LOG.warn("Error on hello/goodbye", e);
                }
                LOG.debug("Hello/Goodbye sent");
                return true;
            }

            LOG.warn("sending selcall not supported");
            throw new IllegalMessageException();

//      if (!(message.length() == MESSAGE_LENGTH && message.matches(String.format("\\d{%d}", MESSAGE_LENGTH)))) {
//        LOG.warn("sending selcall not supported");
//        throw new IllegalMessageException();
//      }
//
//      byte[] b_message = message.getBytes();
//      byte[] setSelcall = {0x02, // <STX>
//        0x63, 0x4b, // cK
//        0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, // ID
//        0x30, 0x30, 0x03}; // 00<ETX>
//
//      for (int i = 3; i < MESSAGE_LENGTH + 3 && i < 11; i++) {
//        setSelcall[i] = b_message[i - 3];
//      }
//
//      byte[] b_counter = counter().getBytes();
//      setSelcall[11] = b_counter[0];
//      setSelcall[12] = b_counter[1];
//
//      try {
//        outputStream.write(setSelcall);
//      } catch (IOException e) {
//        LOG.error(e);
//        return false;
//      }
//
//      LOG.debug("Selcall set");
//
//      //TODO check for radio response
//      try {
//        Thread.sleep(100);
//      } catch (InterruptedException e) {
//        LOG.info("don't wake me!");
//      }
//
//      byte[] sendSelcall = {0x02, 0x70};
//      sendSelcall = ArrayUtils.addAll(sendSelcall, sendCommand.getBytes());
//      sendSelcall = ArrayUtils.addAll(sendSelcall, (byte) 0x20);
//      sendSelcall = ArrayUtils.addAll(sendSelcall, counter().getBytes());
//      sendSelcall = ArrayUtils.addAll(sendSelcall, (byte) 0x03);
//
//      try {
//        outputStream.write(sendSelcall);
//      } catch (IOException e) {
//        LOG.error(e);
//        return false;
//      }
//
//      return true;
        }

        public String getFriendlyName() {
            return friendlyName;
        }

        public final void setFriendlyName(String friendlyName) {
            this.friendlyName = (friendlyName == null ? "" : friendlyName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Transceiver)) {
                return false;
            }

            Transceiver that = (Transceiver) o;

            return port.equals(that.port);
        }

        @Override
        public int hashCode() {
            return port.hashCode();
        }
    }
}
