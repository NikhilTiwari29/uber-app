package com.nikhil.project.uber.uberApp.entities;

import com.nikhil.project.uber.uberApp.enums.PaymentMethod;
import com.nikhil.project.uber.uberApp.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // ----------------------------
    // 🔹 Relationship: Payment → Ride (1:1)
    // ----------------------------
    /**
     * Each payment belongs to exactly one ride.
     *
     * - @OneToOne → One payment entry per ride.
     * - @JoinColumn → Creates foreign key `ride_id` in payment table.
     *
     * Example:
     * - When a ride is completed, a payment entry is created.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false, unique = true)
    private Ride ride;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @CreationTimestamp
    private LocalDateTime paymentTime;
}


/**
 * 🧾 Equivalent SQL Schema:
 *
 CREATE TABLE payment (
 id BIGSERIAL PRIMARY KEY,
 payment_method VARCHAR(255),
 ride_id BIGINT NOT NULL UNIQUE,
 amount DOUBLE PRECISION NOT NULL,
 payment_status VARCHAR(255),
 payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_payment_ride FOREIGN KEY (ride_id) REFERENCES ride(id)
 );
 */
