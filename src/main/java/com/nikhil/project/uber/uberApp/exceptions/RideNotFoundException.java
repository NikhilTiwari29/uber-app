package com.nikhil.project.uber.uberApp.exceptions;

public class RideNotFoundException extends RuntimeException{
    public RideNotFoundException(String message) {
        super(message);
    }
}
