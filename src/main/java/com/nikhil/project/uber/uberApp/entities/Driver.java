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

    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private Double rating;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Boolean available;

    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point currentLocation;
}

/* ============================================================================
   📘 DRIVER ENTITY — FULL DOCUMENTATION
   ============================================================================

   FIELD-WISE DETAILS
   ------------------

   2️⃣ user (Column: user_id)
       Relationship: Driver → User (1:1)
       - A driver is linked to exactly one user
       - @OneToOne → creates a one-to-one mapping
       - @JoinColumn(name = "user_id") → creates user_id column
       - unique = true → ensures one user cannot be assigned to multiple drivers
       - nullable = false → driver must always have a user

   3️⃣ currentLocation (Column: current_location)
       - Stores GIS-based driver location
       - Uses PostGIS geometry: Geometry(Point, 4326)

   SQL EQUIVALENT SCHEMA
   ----------------------
   CREATE TABLE drivers (
       id BIGSERIAL PRIMARY KEY,
       rating DOUBLE PRECISION NOT NULL,
       user_id BIGINT NOT NULL UNIQUE,
       available BOOLEAN,
       current_location Geometry(Point, 4326),
       CONSTRAINT fk_driver_user FOREIGN KEY (user_id) REFERENCES users(id)
   );

   =========================================================================== */
