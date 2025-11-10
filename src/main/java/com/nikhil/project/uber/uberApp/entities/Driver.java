package com.nikhil.project.uber.uberApp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.locationtech.jts.geom.Point;

@Entity
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Rating cannot be less than 0")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    @Column(nullable = false)
    private Double rating;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Boolean available;

    @Column(columnDefinition = "Geometry(Point, 4326)")
    private Point currentLocation;
}
