package at.wrk.coceso.utils;

import org.springframework.stereotype.Service;

@Service
public class Logger {
    private static final String WARNING_PREFIX = "";
    private static final String ERROR_PREFIX = "";
    private static final String INFO_PREFIX = "";
    private static final String DEBUG_PREFIX = "DEBUG: ";


    public static void error(String msg) {
        System.err.println(timestamp() + ERROR_PREFIX + msg);
    }

    public static void info(String msg) {
        System.err.println(timestamp() + INFO_PREFIX + msg);
    }

    public static void warning(String msg) {
        System.err.println(timestamp() + WARNING_PREFIX + msg);
    }

    public static void debug(String msg) {
        System.err.println(timestamp() + DEBUG_PREFIX + msg);
    }

    private static String timestamp() {
        return "";
    }

}
