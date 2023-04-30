package com.peppermint.restusermanager.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;
import java.util.List;
import org.springframework.http.HttpStatus;


@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends CustomException {

    public BadRequestException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(List<String> errorMessages) {
        super(errorMessages, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String errorMessage, Throwable cause) {
        super(errorMessage, cause, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
