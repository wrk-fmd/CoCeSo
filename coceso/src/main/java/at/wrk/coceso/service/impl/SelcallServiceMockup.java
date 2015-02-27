package at.wrk.coceso.service.impl;

import at.wrk.coceso.dao.SelcallDao;
import at.wrk.coceso.entity.Selcall;
import at.wrk.coceso.service.SelcallService;
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
 * Created by Robert on 18.06.2014.
 */
@Service
@Qualifier("mockup")
public class SelcallServiceMockup implements SelcallService {

    @Autowired
    @Qualifier("production")
    SelcallService productionSelcallService;

    @Autowired
    private SelcallDao selcallDao;

    private static final Logger LOG = Logger.getLogger(SelcallServiceMockup.class);

    private final BlockingQueue<DeferredResult<Selcall>> queue = new LinkedBlockingQueue<>();


    private void messageReceived(String message) {
        LOG.info(String.format("Call received from '%s'", message));

        Selcall incomingCall = new Selcall();
        incomingCall.setDirection(Selcall.Direction.RX);
        incomingCall.setTimestamp(new Date());
        incomingCall.setAni(message);

        boolean save = false;
        for(DeferredResult<Selcall> result : queue) {
            save = true;
            LOG.debug("Set result");
            result.setResult(incomingCall);
        }

        if(save)
            selcallDao.save(incomingCall);
    }

    @Override
    public void receiveRequest(String port, DeferredResult<Selcall> result) {
        LOG.debug("Add Result to queue");
        queue.add(result);

        //if(SelcallHandler.getAllStackTraces().isEmpty()) {
            LOG.info("Create new Thread");
            ( new SelcallHandler() ).start();
        //} else {
        //    LOG.debug("Thread already running");
        //}
    }

    @Override
    public List<Selcall> getLastHour() {
        return productionSelcallService.getLastHour();
    }

    @Override
    public boolean sendSelcall(String port, Selcall selcall) {
        if(selcall == null || selcall.getAni() == null) {
            LOG.info("Selcall or ANI is null");
            return false;
        }

        LOG.debug(String.format("Try to send Selcall to '%s'", selcall.getAni()));

        selcall.setTimestamp(new Date());

        boolean success = true;
        String message = selcall.getAni();
        if(!( message.length() == 5 && message.matches(String.format("\\d{%d}", 5)) )) {
            LOG.info("Wrong ANI Format!");

            selcall.setAni("-----");

            success = false;
        }


        selcall.setDirection(success ? Selcall.Direction.TX : Selcall.Direction.TX_FAILED);
        selcallDao.save(selcall);

        return success;
    }

    class SelcallHandler extends Thread {

        @Override
        public void run() {
            while(!queue.isEmpty()) {
                LOG.debug("new Round");

                try {// Sleep 5-10s
                    Thread.sleep(5000);
                    Thread.sleep((long) (Math.random()*5000.0));
                } catch (InterruptedException e) {
                    LOG.warn("interrupted!", e);
                }

                messageReceived( ( (int) (Math.random() * 100000.0) ) + "" );

                try {// Sleep 3-5s
                    Thread.sleep(3000);
                    Thread.sleep((long) (Math.random()*2000.0));
                } catch (InterruptedException e) {
                    LOG.warn("interrupted!", e);
                }

                messageReceived( "12345" );
                messageReceived( "34567" );

                try {// Sleep 6-15s
                    Thread.sleep(6000);
                    Thread.sleep((long) (Math.random()*15000.0));
                } catch (InterruptedException e) {
                    LOG.warn("interrupted!", e);
                }

                // Send last TX selcall as RX out

                List<Selcall> list = getLastHour();
                Selcall selected = null;
                for(Selcall s : list) {
                    if(s.getDirection() == Selcall.Direction.TX) {
                        selected = s;
                    }
                }

                if(selected != null) {
                    messageReceived(selected.getAni());
                }

                LOG.debug("Round finished");
            }
        }
    }
}
