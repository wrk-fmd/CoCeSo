package at.wrk.selcall;

import gnu.io.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Robert on 12.06.2014.
 *
 * RXTX from http://create-lab-commons.googlecode.com/svn/trunk/java/lib/rxtx/
 */
@Component
public class SelcallManager {

    private static final int MESSAGE_LENGTH = 5; // range: 1-8
    private static Logger LOG = Logger.getLogger(SelcallManager.class);

    private static SelcallManager instance = null;

    // Serial Port Settings
    private static final String PORT = "COM6";
    private static final String sendCommand = "Selcall";

    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean portReady = false;

    private ConcurrentLinkedQueue<Byte> queue;

    private int counter = 0;

    private ReceivedMessageListener receivedMessageListener;

    private SelcallManager() {
        this.receivedMessageListener = new ReceivedMessageListener() {
            @Override
            public void handleCall(String message) {
                LOG.info(String.format("Default Listener: incoming call from '%s'", message));
            }
        };

        queue = new ConcurrentLinkedQueue<Byte>();

        openSerialPort();

        helloToTransceiver();
    }

    public static SelcallManager getInstance() {
        if(null == instance) {
            LOG.info("Create Instance");
            instance = new SelcallManager();
        }

        return instance;
    }

    private boolean openSerialPort() {
        CommPortIdentifier identifier;
        try {
            identifier = CommPortIdentifier.getPortIdentifier(PORT);
            if(identifier.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                LOG.error("Port is no serial port!");
                return false;
            }
        } catch (NoSuchPortException e) {
            LOG.error(String.format("No Such Port: %s", PORT), e);
            return false;
        }

        try {
            serialPort = (SerialPort) identifier.open("Selcall by robow", 500);
        } catch (PortInUseException e) {
            LOG.error("Port in use", e);
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

                    switch(serialPortEvent.getEventType()) {
                        case SerialPortEvent.DATA_AVAILABLE:
                            LOG.info("Data available. Trigger #readData()");
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

        if(portReady) {
            LOG.debug("Say goodbye to TRX");
            try {
                sendMessage("TD" + counter());
            } catch (IllegalMessageException e) {
                LOG.error("this should not happen. internal error at goodbye-message", e);
            }
        }

        LOG.info("Close Serial Port");
        serialPort.close();
    }

    private void helloToTransceiver() {
        if(portReady) {
            LOG.debug("Say hello to TRX");
            try {
                sendMessage("TE");
            } catch (IllegalMessageException e) {
                LOG.error("this should not happen. internal error at hello-message", e);
            }
        }
    }

    private String counter() {
        this.counter %= 100;
        return ( ++this.counter < 10 ? "0" : "" ) + this.counter;
    }

    private synchronized void readData() {
        if(!portReady) {
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
                for(int j = 0; j < i; j++) {
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
        while(queue.contains(etx)) {

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
                message.append((char) tmp);
            }
            if (queue.isEmpty() && tmp != 0x03) {
                LOG.info("reached end of queue without 0x03 end byte"); // Should not be possible -> corrupt data
            }

            if (message.substring(0, 1).equals("X") && message.length() == MESSAGE_LENGTH + 3) {
                LOG.info(String.format("Decoded Message: '%s'", message));
                receivedMessageListener.handleCall(message.substring(1, MESSAGE_LENGTH + 1));
            } else {
                LOG.debug(String.format("unknown message: '%s'", message));
            }

        }
    }

    public void setReceivedMessageListener(ReceivedMessageListener listener) {
        this.receivedMessageListener = listener;
    }

    public synchronized boolean sendMessage(String message) throws IllegalMessageException {
        LOG.info(String.format("Try to send message '%s'", message));

        if(!portReady) {
            LOG.warn("Port not ready!");
            return false;
        }

        if(message == null) {
            throw new IllegalMessageException();
        }

        if(message.equals("TE") || message.equals("TD")) {
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

        if(!( message.length() == MESSAGE_LENGTH && message.matches(String.format("\\d{%d}", MESSAGE_LENGTH)) ))
        {
            throw new IllegalMessageException();
        }

        byte[] b_message = message.getBytes();
        byte[] setSelcall = {0x02, // <STX>
                0x63, 0x4b,   // cK
                0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d, 0x2d,  // ID
                0x30, 0x30, 0x03}; // 00<ETX>

        for(int i = 3; i < MESSAGE_LENGTH + 3 && i < 11; i++){
            setSelcall[i] = b_message[i-3];
        }

        byte[] b_counter = counter().getBytes();
        setSelcall[11] = b_counter[0];
        setSelcall[12] = b_counter[1];

        try {
            outputStream.write(setSelcall);
        } catch (IOException e) {
            LOG.error(e);
            return false;
        }

        LOG.debug("Selcall set");

        //TODO check for radio response
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            LOG.info("don't wake me!");
        }


        byte[] sendSelcall = {0x02, 0x70 };
        sendSelcall = ArrayUtils.addAll(sendSelcall, sendCommand.getBytes());
        sendSelcall = ArrayUtils.addAll(sendSelcall, (byte) 0x20);
        sendSelcall = ArrayUtils.addAll(sendSelcall, counter().getBytes());
        sendSelcall = ArrayUtils.addAll(sendSelcall, (byte) 0x03);

        try {
            outputStream.write(sendSelcall);
        } catch (IOException e) {
            LOG.error(e);
            return false;
        }

        return true;
    }


}
