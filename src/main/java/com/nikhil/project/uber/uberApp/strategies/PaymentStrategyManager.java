package com.nikhil.project.uber.uberApp.strategies;

import com.nikhil.project.uber.uberApp.enums.PaymentMethod;
import com.nikhil.project.uber.uberApp.strategies.impl.CashPaymentStrategy;
import com.nikhil.project.uber.uberApp.strategies.impl.WalletPaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyManager {

    private final WalletPaymentStrategy walletPaymentStrategy;
    private final CashPaymentStrategy cashPaymentStrategy;

    public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod) {

        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        return switch (paymentMethod) {
            case WALLET -> walletPaymentStrategy;
            case CASH -> cashPaymentStrategy;
        };
    }
}
