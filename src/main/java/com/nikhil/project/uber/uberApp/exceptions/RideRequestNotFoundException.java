package com.nikhil.project.uber.uberApp.exceptions;

public class RideRequestNotFoundException extends RuntimeException{

    public RideRequestNotFoundException(String message) {
        super(message);
    }
}
