package com.nikhil.project.uber.uberApp.strategies.impl;

import com.nikhil.project.uber.uberApp.entities.Driver;
import com.nikhil.project.uber.uberApp.entities.Payment;
import com.nikhil.project.uber.uberApp.enums.PaymentStatus;
import com.nikhil.project.uber.uberApp.enums.TransactionMethod;
import com.nikhil.project.uber.uberApp.services.PaymentService;
import com.nikhil.project.uber.uberApp.services.WalletService;
import com.nikhil.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentService paymentService;

    @Override
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();

        double platformCommission = payment.getAmount() * PLATFORM_COMMISSION;

        walletService.deductMoneyFromWallet(driver.getUser(),platformCommission, null,
                payment.getRide(), TransactionMethod.RIDE);

        paymentService.updatePaymentStatus(payment, PaymentStatus.CONFIRMED);
    }
}
