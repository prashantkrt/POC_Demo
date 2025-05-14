package com.mylearning.poc.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PsdGenerationException extends RuntimeException {

    public PsdGenerationException(String message) {
        super(message);
    }

    public PsdGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}