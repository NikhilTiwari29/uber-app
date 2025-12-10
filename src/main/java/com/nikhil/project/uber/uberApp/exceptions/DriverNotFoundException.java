package com.nikhil.project.uber.uberApp.exceptions;

public class DriverNotFoundException extends RuntimeException{
    public DriverNotFoundException(String message) {
        super(message);
    }
}
