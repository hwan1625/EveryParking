package com.everyparking.server.exception;

public class ParkingInfoException extends RuntimeException {

    public ParkingInfoException() {

    }


    public ParkingInfoException(String message) {
        super(message);
    }
}
