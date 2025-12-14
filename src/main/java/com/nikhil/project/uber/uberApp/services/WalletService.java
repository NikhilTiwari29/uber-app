package com.nikhil.project.uber.uberApp.services;

import com.nikhil.project.uber.uberApp.entities.Ride;
import com.nikhil.project.uber.uberApp.entities.User;
import com.nikhil.project.uber.uberApp.entities.Wallet;
import com.nikhil.project.uber.uberApp.enums.TransactionMethod;

public interface WalletService {
    Wallet addMoneyToWallet(User user, double amount, String transactionId, Ride ride,
                            TransactionMethod transactionMethod);

    void deductMoneyFromWallet(User user, double amount, String transactionId, Ride ride,
                               TransactionMethod transactionMethod);
    void withDrawAllMoneyFromWallet();
    Wallet findWalletById(Long walletId);
    Wallet createNewWallet(User user);
    Wallet findWalletByUser(User user);
}
