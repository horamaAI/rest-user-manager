package com.peppermint.restusermanager.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.List;
import org.springframework.http.HttpStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(List<String> message) {
        super(message.toString());
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
