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

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Rider rider;

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver driver;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;

    private String otp;

    private Double fare;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;
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


/*
    ----------------------------
    🔹 Relationship: Ride → Rider (Many-to-One)
    ----------------------------

    Many rides can be associated with one rider.

    - @ManyToOne → Many rides belong to one Rider.
    - fetch = LAZY → Rider is loaded only when accessed using ride.getRider().
    - Foreign key: A column named `rider_id` will be created in the rides table that is
      referencing the primary key of the Rider table.
*/

/*
    ----------------------------
    🔹 Relationship: Ride → Driver (Many-to-One)
    ----------------------------

    Many rides can be assigned to one driver.

    - @ManyToOne → A driver can serve multiple rides.
    - fetch = LAZY → Driver is loaded only when accessed via ride.getDriver().
    - Foreign key: A column named `driver_id` will be created in the rides table that is
      referencing the primary key of the Driver table.
*/

/*
    🧾 Equivalent SQL Schema:

    CREATE TABLE ride (
        id BIGSERIAL PRIMARY KEY,
        pickup_location Geometry(Point, 4326),
        drop_off_location Geometry(Point, 4326),
        created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        rider_id BIGINT,
        driver_id BIGINT,
        payment_method VARCHAR(255),
        ride_status VARCHAR(255),
        otp VARCHAR(10),
        fare DOUBLE PRECISION,
        started_at TIMESTAMP,
        ended_at TIMESTAMP,
        CONSTRAINT fk_ride_rider FOREIGN KEY (rider_id) REFERENCES rider(id),
        CONSTRAINT fk_ride_driver FOREIGN KEY (driver_id) REFERENCES driver(id)
    );
*/
