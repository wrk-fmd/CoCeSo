package at.wrk.coceso.parser.staff;

public class CsvParsingException extends Exception {

    public CsvParsingException() {
    }

    public CsvParsingException(String message) {
        super(message);
    }

    public CsvParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
