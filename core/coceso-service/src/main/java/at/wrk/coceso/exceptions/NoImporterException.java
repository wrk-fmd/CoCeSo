package at.wrk.coceso.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class NoImporterException extends RuntimeException {

    public NoImporterException() {
    }

    public NoImporterException(String message) {
        super(message);
    }
}
