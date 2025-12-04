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

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "ride_id", nullable = false, unique = true)
    private Ride ride;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @CreationTimestamp
    private LocalDateTime paymentTime;
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
    🔹 Relationship: Payment → Ride (1:1)
    ----------------------------

    Each payment belongs to exactly one ride.

    - @OneToOne → One payment entry per ride.
    - @JoinColumn → Creates foreign key `ride_id` in payment table.

    Example:
    - When a ride is completed, a payment entry is created.
*/

/*
    🧾 Equivalent SQL Schema:

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
