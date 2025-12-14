package com.nikhil.project.uber.uberApp.exceptions;

public class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(String message) {
        super(message);
    }
}
