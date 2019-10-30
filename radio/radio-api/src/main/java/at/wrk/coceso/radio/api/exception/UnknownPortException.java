package at.wrk.coceso.radio.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownPortException extends Exception {

    public UnknownPortException(String message) {
        super(message);
    }
}
