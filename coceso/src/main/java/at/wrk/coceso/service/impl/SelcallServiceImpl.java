package at.wrk.coceso.service.impl;

import at.wrk.coceso.dao.SelcallDao;
import at.wrk.coceso.entity.Selcall;
import at.wrk.coceso.service.SelcallService;
import at.wrk.selcall.IllegalMessageException;
import at.wrk.selcall.ReceivedMessageListener;
import at.wrk.selcall.SelcallManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;
import java.util.List;
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

    private SelcallManager selcallManager;

    private final BlockingQueue<DeferredResult<Selcall>> queue = new LinkedBlockingQueue<>();

    public SelcallServiceImpl() {
        LOG.info("initialize");


        selcallManager = SelcallManager.getInstance();

        selcallManager.setReceivedMessageListener(new ReceivedMessageListener() {
            @Override
            public void handleCall(String message) {
                messageReceived(message);
            }
        });
    }

    private void messageReceived(String message) {
        LOG.info(String.format("Call received from '%s'", message));

        Selcall incomingCall = new Selcall();
        incomingCall.setDirection(Selcall.Direction.RX);
        incomingCall.setTimestamp(new Date());
        incomingCall.setAni(message);


        selcallDao.save(incomingCall);

        for(DeferredResult<Selcall> result : queue) {
            LOG.debug("Set result");
            result.setResult(incomingCall); // TODO Remove Result?
        }
    }

    public void receiveRequest(DeferredResult<Selcall> result) {
        LOG.debug("Add Result to queue");
        queue.add(result);
    }

    public List<Selcall> getLastHour() {
        LOG.debug("Selcalls of last hour requested");
        // TODO return selcallDao.getLastHour();
        return selcallDao.findAll();
    }

    public boolean sendSelcall(Selcall selcall) {
        if(selcall == null || selcall.getAni() == null) {
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

        return success;
    }
}
