package at.wrk.coceso.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConcernMismatchException extends RuntimeException {

    public ConcernMismatchException(String message) {
        super(message);
    }
}