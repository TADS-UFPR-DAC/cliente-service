package bantads.cliente.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ClienteException extends ResponseStatusException {
    public ClienteException(String message, HttpStatus httpStatus) {
        super(httpStatus, message);
    }
}