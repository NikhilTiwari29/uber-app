package com.nikhil.project.uber.uberApp.entities;

import com.nikhil.project.uber.uberApp.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    // ----------------------------
    // 🔹 Primary Key
    // ----------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;


    // ----------------------------
    // 🔹 User Roles Collection
    // ----------------------------
    /**
     * Represents the set of roles assigned to a user (e.g., ADMIN, DRIVER, USER).
     *
     * 🧠 NOTE:
     * This is **NOT** a One-to-Many relationship — because `Role` is not an Entity.
     * It’s just an Enum (a simple value type), not a separate table mapped to a Java class.
     *
     * 👉 Therefore:
     * - We use `@ElementCollection`, not `@OneToMany`.
     * - There is **no child entity**, so `mappedBy` is **not used**.
     * - The parent (User) fully owns this collection.
     *
     * 🔹 What Hibernate does:
     *   Since collections can’t fit into a single SQL column,
     *   it automatically creates a new table (user_roles) to store these values.
     *
     * 🔹 fetch = FetchType.LAZY:
     *   The roles table is loaded only when you access it,
     *   avoiding unnecessary queries during user retrieval.
     */
    @ElementCollection(fetch = FetchType.LAZY)

    /**
     * @CollectionTable defines details for the extra table Hibernate will create.
     *
     * - `name = "user_roles"` → the name of the table storing the collection data.
     * - `@JoinColumn(name = "user_id")` → defines the foreign key column that links
     *   each role back to its parent user.
     *
     * ⚙️ Why @JoinColumn here but not mappedBy?
     * Because there’s **no child entity class** that could “own” this mapping.
     * So the parent (User) itself defines how the join happens.
     * If this were a true One-to-Many with another Entity, we’d use mappedBy instead.
     */
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )

    /**
     * @Enumerated(EnumType.STRING)
     * 👇 Tells Hibernate how to store Enum values in the database.
     * - ORDINAL → stores numeric position (0,1,2...) ❌ Risky if enum order changes.
     * - STRING  → stores enum names like "ADMIN", "DRIVER", "USER" ✅ Safe & readable.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;
}


/**
 * 🧾 Equivalent SQL Schema:
 *
 * CREATE TABLE users (
 *   id BIGSERIAL PRIMARY KEY,
 *   name VARCHAR(100) NOT NULL,
 *   email VARCHAR(150) NOT NULL UNIQUE,
 *   password VARCHAR(255) NOT NULL
 * );
 *
 * -- ⚙️ Value Collection Table (not an Entity relationship)
 * -- This is not a One-to-Many association, because "roles" is a Set<Enum>, not a Set<Entity>.
 * -- Hibernate still creates a separate table to store these enum values.
 *
 * CREATE TABLE user_roles (
 *   user_id BIGINT NOT NULL,               -- FK linking to users(id)
 *   role VARCHAR(255),                     -- Enum value stored as string
 *   CONSTRAINT fk_user_roles_user
 *       FOREIGN KEY (user_id)
 *       REFERENCES users(id)
 * );
 */
