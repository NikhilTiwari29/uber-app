package com.nikhil.project.uber.uberApp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Rating cannot be less than 0")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    @Column(nullable = false)
    private Double rating;

    // ----------------------------
    // 🔹 Relationship: Driver → User (1:1)
    // ----------------------------
    /**
     * Each Driver is associated with exactly one User.SO that means 1 driver will have only 1 userId
     *
     * - @OneToOne → Defines a one-to-one relationship.
     * - @JoinColumn → will create exactly one column named user_id inside the drivers table,and that column will be
     * a foreign key that will references the primary key (id) of the users table.
     *
     * - unique = true → Ensures one User cannot be linked to multiple Drivers.
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Boolean available;

    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point currentLocation;
}

/**
 * 🧾 Equivalent SQL Schema:
 *
 CREATE TABLE drivers (
 id BIGSERIAL PRIMARY KEY,
 rating DOUBLE PRECISION NOT NULL,
 user_id BIGINT NOT NULL UNIQUE,
 available BOOLEAN,
 current_location Geometry(Point, 4326),
 CONSTRAINT fk_driver_user FOREIGN KEY (user_id) REFERENCES users(id)
 );
 */
