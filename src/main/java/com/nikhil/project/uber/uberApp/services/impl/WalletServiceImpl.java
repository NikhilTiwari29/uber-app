package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.entities.Ride;
import com.nikhil.project.uber.uberApp.entities.User;
import com.nikhil.project.uber.uberApp.entities.Wallet;
import com.nikhil.project.uber.uberApp.entities.WalletTransaction;
import com.nikhil.project.uber.uberApp.enums.TransactionMethod;
import com.nikhil.project.uber.uberApp.enums.TransactionType;
import com.nikhil.project.uber.uberApp.exceptions.WalletNotFoundException;
import com.nikhil.project.uber.uberApp.repositories.WalletRepository;
import com.nikhil.project.uber.uberApp.services.WalletService;
import com.nikhil.project.uber.uberApp.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final ModelMapper modelMapper;
    private final WalletTransactionService walletTransactionService;

    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, double amount, String transactionId, Ride ride,
                                   TransactionMethod transactionMethod) {
        Wallet wallet = findWalletByUser(user);
        wallet.setBalance(wallet.getBalance() + amount);

        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setTransactionId(transactionId);
        walletTransaction.setRide(ride);
        walletTransaction.setWallet(wallet);
        walletTransaction.setTransactionType(TransactionType.CREDIT);
        walletTransaction.setTransactionMethod(transactionMethod);
        walletTransaction.setAmount(amount);

        walletTransactionService.createNewWalletTransaction(walletTransaction);

        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void deductMoneyFromWallet(User user, double amount, String transactionId, Ride ride,
                                      TransactionMethod transactionMethod) {
        Wallet wallet = findWalletByUser(user);

        wallet.setBalance(wallet.getBalance() - amount);

        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setTransactionId(transactionId);
        walletTransaction.setRide(ride);
        walletTransaction.setWallet(wallet);
        walletTransaction.setTransactionType(TransactionType.DEBIT);
        walletTransaction.setTransactionMethod(transactionMethod);
        walletTransaction.setAmount(amount);
        walletTransactionService.createNewWalletTransaction(walletTransaction);

        walletRepository.save(wallet);
    }

    @Override
    public void withDrawAllMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletById(Long walletId) {
        return walletRepository.findById(walletId).orElseThrow(() ->
                new WalletNotFoundException(
                        "Wallet not found with id: " + walletId
                ));
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletByUser(User user) {
        return walletRepository.findById(user.getId()).orElseThrow(() ->
                new WalletNotFoundException(
                        "Wallet not found for user with id: " + user.getId()
                ));
    }
}
