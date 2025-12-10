package com.nikhil.project.uber.uberApp.exceptions;

public class RiderNotFoundException extends RuntimeException{
    public RiderNotFoundException(String message) {
        super(message);
    }
}
