package at.wrk.coceso.utils;

import org.springframework.stereotype.Service;

import java.util.logging.*; // cannot import Logger only, because of the class name conflict!

@Service
public class Logger {
    private static final String WARNING_PREFIX = "";
    private static final String ERROR_PREFIX = "";
    private static final String INFO_PREFIX = "";
    private static final String DEBUG_PREFIX = "DEBUG: ";

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger("CoCeSo");

    public static void error(String msg) {
        System.err.println(timestamp() + ERROR_PREFIX + msg);
        log.severe(msg);
    }

    public static void info(String msg) {
        System.err.println(timestamp() + INFO_PREFIX + msg);
        log.info(msg);
    }

    public static void warning(String msg) {
        System.err.println(timestamp() + WARNING_PREFIX + msg);
        log.warning(msg);
    }

    public static void debug(String msg) {
        System.err.println(timestamp() + DEBUG_PREFIX + msg);
        log.fine(msg);
    }

    private static String timestamp() {
        return "";
    }

}
