package at.wrk.coceso.radio.exception;

public class PortException extends Exception {

    private final String port;

    public PortException(String port, String s) {
        super(s);
        this.port = port;
    }

    public PortException(String port, String s, Throwable throwable) {
        super(s, throwable);
        this.port = port;
    }

    @Override
    public String getMessage() {
        return String.format("%s (Port: %s)", super.getMessage(), port);
    }
}
