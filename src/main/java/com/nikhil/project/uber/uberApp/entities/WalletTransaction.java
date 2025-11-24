package com.nikhil.project.uber.uberApp.entities;

import com.nikhil.project.uber.uberApp.enums.TransactionMethod;
import com.nikhil.project.uber.uberApp.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionMethod transactionMethod;

    // ----------------------------
    // 🔹 Relationship: WalletTransaction → Ride (1:1)
    // ----------------------------
    /**
     * A transaction may be linked to a specific ride.
     *
     * - @OneToOne → One transaction maps to one ride.
     * - @JoinColumn → Foreign key column: ride_id
     *
     * Use cases:
     * - Deduct fare after completing a ride
     * - Refund after ride cancellation
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @Column(nullable = false, unique = true)
    private String transactionId;

    // ----------------------------
    // 🔹 Relationship: WalletTransaction → Wallet (Many-to-One)
    // ----------------------------
    /**
     * Many transactions belong to one wallet.
     *
     * - @ManyToOne → One wallet can contain many transactions.
     * - @JoinColumn → Creates wallet_id in wallet_transaction table.
     *
     * Example:
     * wallet.addMoney(...) → creates one CREDIT transaction
     * wallet.deductFare(...) → creates one DEBIT transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @CreationTimestamp
    private LocalDateTime timeStamp;
}


/**
 * 🧾 Equivalent SQL Schema:
 *
 CREATE TABLE wallet_transaction (
 id BIGSERIAL PRIMARY KEY,
 amount DOUBLE PRECISION,
 transaction_type VARCHAR(255),
 transaction_method VARCHAR(255),
 ride_id BIGINT,
 wallet_id BIGINT NOT NULL,
 transaction_id VARCHAR(255) NOT NULL UNIQUE,
 time_stamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

 CONSTRAINT fk_wallet_txn_ride   FOREIGN KEY (ride_id)   REFERENCES ride(id),
 CONSTRAINT fk_wallet_txn_wallet FOREIGN KEY (wallet_id) REFERENCES wallet(id)
 );
 */
