package com.nikhil.project.uber.uberApp.entities;

import com.nikhil.project.uber.uberApp.enums.PaymentMethod;
import com.nikhil.project.uber.uberApp.enums.RideStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point pickupLocation;

    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point dropOffLocation;

    @CreationTimestamp
    private LocalDateTime createdTime;

    // ----------------------------
    // 🔹 Relationship: Ride → Rider (Many-to-One)
    // ----------------------------
    /**
     * Many rides can be associated with one rider.
     *
     * - @ManyToOne → Many rides belong to one Rider.
     * - fetch = LAZY → Rider is loaded only when accessed using ride.getRider().
     * - Foreign key: A column named `rider_id` will be created in the rides table that is
     *   referencing the primary key of the Rider table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Rider rider;

    // ----------------------------
    // 🔹 Relationship: Ride → Driver (Many-to-One)
    // ----------------------------
    /**
     * Many rides can be assigned to one driver.
     *
     * - @ManyToOne → A driver can serve multiple rides.
     * - fetch = LAZY → Driver is loaded only when accessed via ride.getDriver().
     * - Foreign key: A column named `driver_id` will be created in the rides table that is referencing
     *   the primary key of the Driver table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Driver driver;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;

    private Double fair;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;
}


/**
 * 🧾 Equivalent SQL Schema:
 *
 CREATE TABLE ride (
 id BIGSERIAL PRIMARY KEY,
 pickup_location Geometry(Point, 4326),
 drop_off_location Geometry(Point, 4326),
 created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 rider_id BIGINT,
 driver_id BIGINT,
 payment_method VARCHAR(255),
 ride_status VARCHAR(255),
 CONSTRAINT fk_ride_rider FOREIGN KEY (rider_id) REFERENCES rider(id),
 CONSTRAINT fk_ride_driver FOREIGN KEY (driver_id) REFERENCES driver(id)
 );
 */
