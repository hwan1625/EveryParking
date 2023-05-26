package com.everyparking.server.exception;

public class CarValidationException extends RuntimeException {

    public CarValidationException() {

    }


    public CarValidationException(String message) {
        super(message);
    }
}
