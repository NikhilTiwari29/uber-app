package com.nikhil.project.uber.uberApp.exceptions;

public class RideStatusNotConfirmedException extends RuntimeException{
    public RideStatusNotConfirmedException(String message) {
        super(message);
    }
}
