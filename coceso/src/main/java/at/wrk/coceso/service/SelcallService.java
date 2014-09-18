package at.wrk.coceso.service;

import at.wrk.coceso.entity.Selcall;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

/**
 * Created by Robert on 18.06.2014.
 */
public interface SelcallService {

    public void receiveRequest(DeferredResult<Selcall> result);

    public List<Selcall> getLastHour();

    public boolean sendSelcall(Selcall selcall);
}
