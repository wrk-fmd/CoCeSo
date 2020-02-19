package at.wrk.coceso.exceptions;

public class ConcernException extends Exception {

    public ConcernException(String message) {
        super(message);
    }

    public ConcernException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
