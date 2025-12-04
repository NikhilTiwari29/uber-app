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

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @CreationTimestamp
    private LocalDateTime timeStamp;
}

/*
   ❗ Why we do NOT use @Data in JPA entities

   Lombok's @Data generates:
   - getters & setters
   - equals()
   - hashCode()
   - toString()

   These cause MAJOR PROBLEMS with JPA:

   1️⃣ equals() & hashCode() generate issues with Hibernate proxies
       - Hibernate wraps entities with proxy classes
       - equals/hashCode using all fields can trigger unexpected queries
       - Can break entity identity and persistence behavior

   2️⃣ toString() can cause infinite loops
       - If an entity references another entity with a relationship (e.g., Driver → User → Driver)
       - @Data toString() prints all fields → leads to recursive calls → StackOverflowError

   3️⃣ Performance issues
       - equals(), hashCode() including all columns create heavy operations
       - Can trigger lazy loading unintentionally → massive performance hits

   4️⃣ JPA's requirement for controlled getters/setters
       - Entities should have simple getters/setters
       - equals/hashCode should usually be based ONLY on the primary key (id)

   ✔ Therefore: We use @Getter and @Setter ONLY
   ✔ And avoid @Data to prevent proxy issues, recursion, and unexpected lazy loading
*/


/* =======================================================
   COMMENT FOR @OneToOne WalletTransaction → Ride (1:1)
   =======================================================

   A transaction may be linked to a specific ride.

   - @OneToOne → One transaction maps to one ride.
   - @JoinColumn → Foreign key column: ride_id

   Use cases:
   - Deduct fare after completing a ride
   - Refund after ride cancellation
*/

/* ==========================================================
   COMMENT FOR @ManyToOne WalletTransaction → Wallet (N:1)
   ==========================================================

   Many transactions belong to one wallet.

   - @ManyToOne → One wallet can contain many transactions.
   - @JoinColumn → Creates wallet_id in wallet_transaction table.

   Example:
   wallet.addMoney(...) → creates one CREDIT transaction
   wallet.deductFare(...) → creates one DEBIT transaction
*/

/* ==============================
      Equivalent SQL Schema
   ==============================

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
