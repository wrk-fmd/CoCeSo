package at.wrk.coceso.service.impl;

import at.wrk.coceso.dao.SelcallDao;
import at.wrk.coceso.entity.Selcall;
import at.wrk.coceso.service.SelcallService;
import at.wrk.selcall.IllegalMessageException;
import at.wrk.selcall.ReceivedMessageListener;
import at.wrk.selcall.TransceiverManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Robert on 14.06.2014.
 */
@Service
@Qualifier("production")
public class SelcallServiceImpl implements SelcallService {

    private static final
    Logger LOG = Logger.getLogger(SelcallServiceImpl.class);

    @Autowired
    private SelcallDao selcallDao;

    private TransceiverManager transceiverManager;

    private final Map<String, BlockingQueue<DeferredResult<Selcall>>> queues = new HashMap<>();

    private Set<String> availableSerialPorts = null;

    public SelcallServiceImpl() {
        LOG.info("initialize");


        transceiverManager = TransceiverManager.getInstance();

        availableSerialPorts = TransceiverManager.getAllAvailableSerialPorts();
        for( final String port : availableSerialPorts ) {
            queues.put(port, new LinkedBlockingQueue<DeferredResult<Selcall>>());

            transceiverManager.setReceivedMessageListener(port, new ReceivedMessageListener() {
                @Override
                public void handleCall(String message) {
                    messageReceived(port, message);
                }
            });
        }

    }

    private void messageReceived(String port, String message) {
        LOG.info(String.format("Call received from '%s'", message));

        Selcall incomingCall = new Selcall(); //TODO add port to Selcall DB-table
        incomingCall.setDirection(Selcall.Direction.RX);
        incomingCall.setTimestamp(new Date());
        incomingCall.setAni(message);


        selcallDao.save(incomingCall);
        if(!queues.keySet().contains(port)) {
            LOG.warn(String.format("Transceiver on port '%s' not found!", port));
            return;
        }
        Iterator<DeferredResult<Selcall>> iterator = queues.get(port).iterator();
        while( iterator.hasNext() ) {
            DeferredResult<Selcall> result = iterator.next();
            LOG.debug("Set result");
            result.setResult(incomingCall);
            iterator.remove();
        }
    }

    public void receiveRequest(String port, DeferredResult<Selcall> result) {
        LOG.debug("Add Result to queue");
        if(!availableSerialPorts.contains(port)) {
            LOG.info(String.format("port not found ('%s')", port));
        }
        queues.get(port).add(result);
    }

    public List<Selcall> getLastHour() {
        LOG.debug("Selcalls of last hour requested. NOT FULLY IMPLEMENTED");
        // TODO return selcallDao.getLastHour();
        return selcallDao.findAll();
    }

    public boolean sendSelcall(String port, Selcall selcall) {
        throw new UnsupportedOperationException();

        /*if(selcall == null || selcall.getAni() == null) {
            LOG.info("Selcall or ANI is null");
            return false;
        }

        LOG.debug(String.format("Try to send Selcall to '%s'", selcall.getAni()));

        selcall.setTimestamp(new Date());

        boolean success;
        try {
            success = selcallManager.sendMessage(selcall.getAni());
        } catch (IllegalMessageException e) {
            LOG.info("Wrong ANI Format!", e);

            selcall.setAni("-----");

            success = false;
        }


        selcall.setDirection(success ? Selcall.Direction.TX : Selcall.Direction.TX_FAILED);
        selcallDao.save(selcall);

        return success;*/
    }
}
