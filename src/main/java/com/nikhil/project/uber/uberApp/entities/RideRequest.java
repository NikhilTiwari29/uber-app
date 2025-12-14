package com.nikhil.project.uber.uberApp.entities;

import com.nikhil.project.uber.uberApp.enums.PaymentMethod;
import com.nikhil.project.uber.uberApp.enums.RideRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Rider rider;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private RideRequestStatus rideRequestStatus;

    private Double fare;
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
    🔹 Relationship: RideRequest → Rider (Many-to-One)
    ----------------------------

    Many ride requests in this table can belong to one rider from the rider table.

    - @ManyToOne → Many ride requests per rider.
    - fetch = LAZY → Rider will be loaded only when it is accessed in java code. For e.g: rideRequest.getRider();
    - A foreign key column (rider_id) will be created in ride_requests table and
      that will be mapped to primary key of rider table
*/

/*
    🧾 Equivalent SQL Schema:

    CREATE TABLE ride_request (
        id BIGSERIAL PRIMARY KEY,
        pickup_location Geometry(Point, 4326),
        drop_off_location Geometry(Point, 4326),
        requested_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        rider_id BIGINT,
        payment_method VARCHAR(255),
        ride_request_status VARCHAR(255),
        fare DOUBLE PRECISION,
        CONSTRAINT fk_riderequest_rider FOREIGN KEY (rider_id) REFERENCES rider(id)
    );
*/
