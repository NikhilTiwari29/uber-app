package com.nikhil.project.uber.uberApp.services;

import com.nikhil.project.uber.uberApp.entities.Payment;
import com.nikhil.project.uber.uberApp.entities.Ride;

public interface PaymentService {
    void processPayment(Payment payment);
    Payment createNewPayment(Ride ride);
}
