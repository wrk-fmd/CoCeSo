package at.wrk.coceso.utils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;



@Service
@Deprecated
public class CocesoLogger {
    private static final String WARNING_PREFIX = "";
    private static final String ERROR_PREFIX = "";
    private static final String INFO_PREFIX = "";
    private static final String DEBUG_PREFIX = "";

    private static final Logger log = Logger.getLogger("CoCeSo");

    public static void error(String msg) {
        //System.err.println(timestamp() + ERROR_PREFIX + msg);
        log.error(msg);
    }

    public static void info(String msg) {
        //System.err.println(timestamp() + INFO_PREFIX + msg);
        log.info(msg);
    }

    public static void warn(String msg) {
        //System.err.println(timestamp() + WARNING_PREFIX + msg);
        log.warn(msg);
    }

    public static void debug(String msg) {
        //System.err.println(timestamp() + DEBUG_PREFIX + msg);
        log.debug(msg);
    }

    private static String timestamp() {
        return "";
    }

}
