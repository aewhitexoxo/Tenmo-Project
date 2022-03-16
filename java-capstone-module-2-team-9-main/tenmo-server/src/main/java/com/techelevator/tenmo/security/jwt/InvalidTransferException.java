package com.techelevator.tenmo.security.jwt;

public class InvalidTransferException extends Exception{

    public InvalidTransferException(String msg, Throwable t) {
        super(msg, t);
    }
    public InvalidTransferException(String msg) {
        super(msg);
    }
}

