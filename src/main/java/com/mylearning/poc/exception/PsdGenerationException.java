package com.mylearning.poc.exception;

public class PsdGenerationException extends RuntimeException {
    public PsdGenerationException(String message) {
        super(message);
    }

    public PsdGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}