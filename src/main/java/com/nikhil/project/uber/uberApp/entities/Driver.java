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

/*
   ========================================================================
   ❗ Why we use optional = false in @OneToOne (Driver → User)
   ========================================================================

   optional = false (JPA-level constraint)
   ---------------------------------------
   - Means the Driver entity CANNOT exist without a User.
   - Hibernate will throw an error BEFORE executing SQL if driver.user is null.
   - Ensures that in Java code:
        new Driver().save()
     is NOT allowed unless a User is assigned.

   nullable = false (Database-level constraint)
   --------------------------------------------
   - Enforces NOT NULL on the "user_id" column in the database.
   - Prevents inserting NULL via SQL or another microservice.
   - Both optional=false AND nullable=false protect data at different layers.

   Why this makes sense here:
   --------------------------
   - A Driver MUST always be a User in the system.
   - Business rule: No driver exists without a user profile.
*/

/*
   ========================================================================
   ❌ Why we DO NOT use cascade = CascadeType.ALL in Driver → User
   ========================================================================

   Driver is NOT the parent of User.
   ---------------------------------
   - User is a high-level, global entity.
   - Driver is "just a role" of a user.
   - User should NEVER be deleted or auto-modified when Driver changes.

   What goes wrong with cascade:
   -----------------------------
   If we did:

       @OneToOne(cascade = CascadeType.ALL)
       private User user;

   Then:

       driverRepository.delete(driver);

   Would also delete:
       ❌ the User row
       ❌ associated Rider entity (if exists)
       ❌ associated Wallet
       ❌ associated WalletTransactions
       ❌ associated RideRequests
       ❌ basically the entire user account

   This is DANGEROUS and breaks your whole app.

   General rule:
   -------------
   ❌ Never cascade from child → parent.
   ❌ Never cascade from an entity linked to User, Rider, Driver, Payment, Ride.
   ✔ Only cascade from true parent → true child (e.g., Wallet → Transactions)
*/

