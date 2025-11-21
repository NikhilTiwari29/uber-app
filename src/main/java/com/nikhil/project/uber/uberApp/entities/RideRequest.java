package com.nikhil.project.uber.uberApp.entities;

import com.nikhil.project.uber.uberApp.enums.PaymentMethod;
import com.nikhil.project.uber.uberApp.enums.RideRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point pickupLocation;
    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point dropOffLocation;
    @CreationTimestamp
    private LocalDateTime requestedTime;

    // ----------------------------
    // 🔹 Relationship: RideRequest → Rider (Many-to-One)
    // ----------------------------
    /**
     * Many ride requests in this table can belong to one rider from the rider table.
     *
     * - @ManyToOne → Many ride requests per rider.
     * - fetch = LAZY → Rider will be loaded only when it is accessed in java code.For e.g:-rideRequest.getRider();
     * - A foreign key column (rider_id) will be created in ride_requests table and
     *  that will be mapped to primary key of rider table
     *
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Rider rider;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private RideRequestStatus rideRequestStatus;
}


/**
 * 🧾 Equivalent SQL Schema:
 *
 CREATE TABLE ride_request (
 id BIGSERIAL PRIMARY KEY,
 pickup_location Geometry(Point, 4326),
 drop_off_location Geometry(Point, 4326),
 requested_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 rider_id BIGINT,
 payment_method VARCHAR(255),
 ride_request_status VARCHAR(255),
 CONSTRAINT fk_riderequest_rider FOREIGN KEY (rider_id) REFERENCES rider(id)
 );
 */
