package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.entities.Payment;
import com.nikhil.project.uber.uberApp.entities.Ride;
import com.nikhil.project.uber.uberApp.exceptions.PaymentRepository;
import com.nikhil.project.uber.uberApp.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {
    }

    @Override
    public Payment createNewPayment(Ride ride) {
        return null;
    }
}
