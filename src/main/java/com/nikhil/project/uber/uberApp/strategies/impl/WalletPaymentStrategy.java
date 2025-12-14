package com.nikhil.project.uber.uberApp.strategies.impl;

import com.nikhil.project.uber.uberApp.entities.Driver;
import com.nikhil.project.uber.uberApp.entities.Payment;
import com.nikhil.project.uber.uberApp.entities.Rider;
import com.nikhil.project.uber.uberApp.enums.PaymentStatus;
import com.nikhil.project.uber.uberApp.enums.TransactionMethod;
import com.nikhil.project.uber.uberApp.services.PaymentService;
import com.nikhil.project.uber.uberApp.services.WalletService;
import com.nikhil.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentService paymentService;

    @Override
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();
        Rider rider = payment.getRide().getRider();
        walletService.deductMoneyFromWallet(rider.getUser(), payment.getAmount(), null,
                payment.getRide(), TransactionMethod.RIDE);

        double driverCut = payment.getAmount() * (1 - PLATFORM_COMMISSION);
        walletService.deductMoneyFromWallet(driver.getUser(), driverCut , null,
                payment.getRide(), TransactionMethod.RIDE);

        paymentService.updatePaymentStatus(payment, PaymentStatus.CONFIRMED);
    }
}
