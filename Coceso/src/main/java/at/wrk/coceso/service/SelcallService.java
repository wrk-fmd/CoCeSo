package at.wrk.coceso.service;

import at.wrk.coceso.dao.SelcallDao;
import at.wrk.coceso.entity.Selcall;
import at.wrk.selcall.IllegalMessageException;
import at.wrk.selcall.ReceivedMessageListener;
import at.wrk.selcall.SelcallManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Robert on 14.06.2014.
 */
@Service
public class SelcallService {

    private static final
    Logger LOG = Logger.getLogger(SelcallService.class);

    @Autowired
    private SelcallDao selcallDao;

    private SelcallManager selcallManager;

    private final BlockingQueue<DeferredResult<Selcall>> queue = new LinkedBlockingQueue<>();

    public SelcallService() {
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
            result.setResult(incomingCall);
        }
    }

    public void receiveRequest(DeferredResult<Selcall> result) {
        LOG.debug("Add Result to queue");
        queue.add(result);
    }

    public List<Selcall> getLastHour() {
        LOG.debug("Selcalls of last hour requested");
        //return selcallDao.getLastHour();
        return new LinkedList<>();
    }

    public boolean sendSelcall(Selcall selcall) {
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
