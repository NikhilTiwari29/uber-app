package com.nikhil.project.uber.uberApp.exceptions;

public class RideCanNotBeCancelledException extends RuntimeException{
    public RideCanNotBeCancelledException(String message) {
        super(message);
    }
}
