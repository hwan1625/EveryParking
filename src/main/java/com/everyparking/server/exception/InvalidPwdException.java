package com.everyparking.server.exception;

public class InvalidPwdException extends RuntimeException{


    public InvalidPwdException() {

    }
    public InvalidPwdException(String message) {
        super(message);
    }
}
