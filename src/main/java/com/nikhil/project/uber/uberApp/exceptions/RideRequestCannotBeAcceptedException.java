package com.nikhil.project.uber.uberApp.exceptions;

public class RideRequestCannotBeAcceptedException extends RuntimeException{

    public RideRequestCannotBeAcceptedException(String message) {
        super(message);
    }
}
