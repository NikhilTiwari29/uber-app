package com.nikhil.project.uber.uberApp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Rating cannot be less than 0")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    @Column(nullable = false)
    private Double rating;

    // ----------------------------
    // 🔹 Relationship: Rider → User (1:1)
    // ----------------------------
    /**
     * Each Rider is associated with exactly one User.
     *
     * - @OneToOne → Defines a one-to-one relationship.
     * - @JoinColumn → Specifies the foreign key column (user_id) in the riders table.
     *   It references the "id" column of the users table.
     * - unique = true → Ensures one User can be linked to only one Rider.
     *
     * This is a **unidirectional** mapping — Rider owns the foreign key.
     */
    @OneToOne(optional = false) // User is mandatory (no rider without user)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}

/**
 * 🧾 Equivalent SQL Schema:
 *
     CREATE TABLE riders (
     id BIGSERIAL PRIMARY KEY,
     rating DOUBLE PRECISION NOT NULL DEFAULT 0.0,
     user_id BIGINT NOT NULL UNIQUE,
     CONSTRAINT fk_rider_user FOREIGN KEY (user_id) REFERENCES users(id)
     );
 */
