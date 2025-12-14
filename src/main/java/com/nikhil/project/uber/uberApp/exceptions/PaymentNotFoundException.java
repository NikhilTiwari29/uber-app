package com.nikhil.project.uber.uberApp.exceptions;

public class PaymentNotFoundException extends RideNotFoundException{
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
