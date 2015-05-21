package at.wrk.selcall;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TransceiverManagerMockup extends AbstractTransceiverManager {

    private static TransceiverManagerMockup instance = null;

    private static final Logger LOG = Logger.getLogger(TransceiverManagerMockup.class);

    private final Set<String> ports = new HashSet<>(Arrays.asList("VIRT1", "VIRT2"));

    private MessageGenerator generator = null;

    private TransceiverManagerMockup() {
        super();
        init();
    }

    public static synchronized TransceiverManagerMockup getInstance() {
        if (instance == null) {
            instance = new TransceiverManagerMockup();
        }
        return instance;
    }

    protected void init() {
        if(generator == null) {
            LOG.info("Create new Thread");
            generator = new MessageGenerator();
            generator.start();
        }
        else
            LOG.warn("mulitple call of init!!");
    }

    @Override
    public void shutdown() {
        LOG.info("Stopping Generator");
        if (generator != null) {
            generator.stopGenerating();
        }
    }

    @Override
    public Set<String> getPorts() {
        return ports;
    }

    @Override
    public void send(String message) {
        handleMessage(null, message);
    }

    @Override
    public void send(String port, String message) {
        handleMessage(port, message);
    }

    class MessageGenerator extends Thread {

        private volatile boolean active = true;

        @Override
        public void run() {
            while (active) {
                LOG.debug("new Round");

                try {// Sleep 30-40s
                    Thread.sleep(30000);
                    Thread.sleep((long) (Math.random() * 10000.0));
                } catch (InterruptedException e) {
                    LOG.warn("interrupted!", e);
                }

                if (Math.random() < 0.1) {
                    handleEmergency("VIRT1", "34567");
                } else {
                    handleMessage("VIRT1", "34567");
                }

                try {// Sleep 10-15s
                    Thread.sleep(10000);
                    Thread.sleep((long) (Math.random() * 5000.0));
                } catch (InterruptedException e) {
                    LOG.warn("interrupted!", e);
                }

                if (Math.random() < 0.1) {
                    handleEmergency("VIRT2", "12345");
                } else {
                    handleMessage("VIRT2", "12345");
                }

                try {// Sleep 20-25s
                    Thread.sleep(20000);
                    Thread.sleep((long) (Math.random() * 5000.0));
                } catch (InterruptedException e) {
                    LOG.warn("interrupted!", e);
                }

                LOG.debug("Round finished");
            }
        }

        public void stopGenerating() {
            active = false;
            this.interrupt();
        }
    }
}
