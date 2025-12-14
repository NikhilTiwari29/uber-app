package com.nikhil.project.uber.uberApp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rider {

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
    🔹 Relationship: Rider → User (1:1)
    ----------------------------

    Each Rider is associated with exactly one User.

    - @OneToOne → Defines a one-to-one relationship.
    - @JoinColumn → Specifies the foreign key column (user_id) in the riders table.
      It references the "id" column of the users table.
    - unique = true → Ensures one User can be linked to only one Rider.

    This is a **unidirectional** mapping — Rider owns the foreign key.
*/

/*
    🧾 Equivalent SQL Schema:

        CREATE TABLE riders (
            id BIGSERIAL PRIMARY KEY,
            rating DOUBLE PRECISION NOT NULL DEFAULT 0.0,
            user_id BIGINT NOT NULL UNIQUE,
            CONSTRAINT fk_rider_user FOREIGN KEY (user_id) REFERENCES users(id)
        );
*/
