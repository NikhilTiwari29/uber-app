package com.nikhil.project.uber.uberApp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Double balance;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WalletTransaction> transactions;
}

/* =====================================
   COMMENT FOR @OneToOne Wallet → User
   =====================================

   Each Wallet belongs to exactly one User.

   - @OneToOne → Defines a one-to-one relationship.
   - fetch = LAZY → User details load only on access.
   - @JoinColumn → Creates user_id in wallet table.

   This is the owning side (Wallet owns the FK).
*/

/* =============================================
   COMMENT FOR @OneToMany Wallet → Transactions
   =============================================

   One wallet can have multiple transaction records.

   - mappedBy = "wallet"
         WalletTransaction owns the foreign key (wallet_id).

   - fetch = LAZY
         Transactions are loaded only when wallet.getTransactions() is called.

   - cascade = CascadeType.ALL
         ✔ PERSIST → Saving wallet also saves transactions
         ✔ MERGE   → Merging wallet merges transactions
         ✔ REMOVE  → Deleting wallet deletes all transactions
         ✔ REFRESH → Refresh syncs children
         ✔ DETACH  → Detaching wallet detaches transactions

   This is useful because transactions logically belong to the wallet.
   If a wallet is deleted, its transaction history should also be deleted.
*/

/* ==============================
      Equivalent SQL Schema
   ==============================

   CREATE TABLE wallet (
       id BIGSERIAL PRIMARY KEY,
       user_id BIGINT NOT NULL UNIQUE,
       balance DOUBLE PRECISION,
       CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id)
   );
*/
