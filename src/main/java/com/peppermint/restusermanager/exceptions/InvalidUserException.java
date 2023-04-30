package com.peppermint.restusermanager.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidUserException extends CustomException {

    public InvalidUserException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidUserException(List<String> errorMessages) {
        super(errorMessages, HttpStatus.BAD_REQUEST);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }

    public InvalidUserException(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
