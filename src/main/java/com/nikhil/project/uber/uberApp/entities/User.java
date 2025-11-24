package com.nikhil.project.uber.uberApp.entities;

import com.nikhil.project.uber.uberApp.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
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
     * Stores the list of roles assigned to a user (e.g., ADMIN, DRIVER, USER).
     *
     * ❗ IMPORTANT:
     * This is **not** a One-to-Many relationship because `Role` is an enum, not an entity.
     * Since enums cannot be stored as a single column inside the `users` table (because this is a collection),
     * Hibernate creates a **separate table** to store these values.
     *
     * Therefore → `@ElementCollection` is used.
     *
     * 🔹 How Hibernate stores this collection:
     *   A new table named `user_roles` is created with the following structure:
     *
     *       user_roles (
     *           user_id   BIGINT  — FK to users.id
     *           role      VARCHAR — enum value stored as STRING
     *       )
     *
     * 🔹 LAZY loading:
     *   The `user_roles` table is queried **only when `user.getRoles()` is accessed**,
     *   which improves performance during regular user lookups.
     */
    @ElementCollection(fetch = FetchType.LAZY)

/**
 * Specifies details of the collection table that stores user roles.
 *
 * - name = "user_roles"
 *       The table where all roles are stored.
 *
 * - @JoinColumn(name = "user_id")
 *       Defines the foreign key column inside `user_roles` that links
 *       each role entry back to its parent user.
 *
 * ❗ Why `@JoinColumn` and not `mappedBy`?
 *    Because there is **no child entity** here.
 *    This is not a bidirectional relationship.
 *    User controls the table mapping, so User must specify the join details.
 */
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )

/**
 * Stores enum values as strings (e.g., "ADMIN", "RIDER") rather than numeric ordinals.
 *
 * ✔ STRING  → safe, readable, database-friendly
 * ✘ ORDINAL → risky (breaks if enum order changes)
 */
    @Enumerated(EnumType.STRING)

/**
 * The name of the column inside the `user_roles` table
 * that holds the individual role values.
 *
 * ⚠️ NOTE:
 * This column does **not** appear in the `users` table.
 * It appears inside the automatically created `user_roles` table.
 */
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
