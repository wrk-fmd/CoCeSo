package at.wrk.coceso.plugins.vienna;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public class AlwaysReuseConnectionStrategy implements ConnectionReuseStrategy {
    @Override
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        return true;
    }
}
