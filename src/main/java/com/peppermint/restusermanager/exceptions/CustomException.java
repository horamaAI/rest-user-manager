package com.peppermint.restusermanager.exceptions;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final List<String> errors;

    private final HttpStatus status;

    public CustomException(String errorMessage, HttpStatus status) {
        super(errorMessage);
        this.status = status;
        this.errors = Collections.singletonList(errorMessage);
        System.out.println("xoxo customexc constructor: " + status);

    }

    public CustomException(List<String> errorMessages, HttpStatus status) {
        this.status = status;
        this.errors = errorMessages;
    }

    public CustomException(String errorMessage, Throwable cause, HttpStatus status) {
        super(errorMessage, cause);
        this.status = status;
        errors = Collections.singletonList(errorMessage);
    }

    public CustomException(Throwable cause, HttpStatus status) {
        super(cause);
        this.status = status;
        errors = null;
    }

}
