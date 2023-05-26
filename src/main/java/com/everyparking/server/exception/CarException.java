package com.everyparking.server.exception;

public class CarException extends RuntimeException {

    public CarException() {

    }

    public CarException(String message) {
        super(message);
    }
}
