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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;
}

/* ============================
       COMMENT FOR PRIMARY KEY
   ============================

   - @Id → Marks primary key
   - @GeneratedValue → Auto-generated using identity strategy
*/

/* ====================================
   COMMENT FOR @ElementCollection(roles)
   ====================================

   Stores the list of roles assigned to a user (e.g., ADMIN, DRIVER, USER).

   This is NOT a One-to-Many relationship because Role is an ENUM (value type)
   and NOT an entity.

   Therefore Hibernate creates a SEPARATE TABLE to store this collection:

       user_roles (
           user_id BIGINT,
           role VARCHAR
       )

   LAZY loading → This table is queried only when user.getRoles() is called.
*/

/* =====================================
   COMMENT FOR @CollectionTable(…)
   =====================================

   - name = "user_roles"
         Defines the table that stores user-role mappings.

   - joinColumns = @JoinColumn(name = "user_id")
         The foreign key column inside user_roles table pointing to users.id.

   Why NOT `mappedBy`?
   Because there is no child entity. This is NOT a bidirectional relationship.
*/

/* =====================================
   COMMENT FOR @Enumerated(EnumType.STRING)
   =====================================

   Stores enum values as STRING ("ADMIN", "RIDER") instead of ORDINAL (0,1,2…).

   ✔ STRING → readable, safe
   ✘ ORDINAL → risky if enum order changes
*/

/* =====================================
   COMMENT FOR @Column(name = "role")
   =====================================

   This column exists in the user_roles table (NOT in users table).

   It stores the actual enum value for each assigned role.
*/

/* ==============================
      Equivalent SQL Schema
   ==============================

   CREATE TABLE users (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(100) NOT NULL,
     email VARCHAR(150) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL
   );

   CREATE TABLE user_roles (
     user_id BIGINT NOT NULL,
     role VARCHAR(255),
     FOREIGN KEY (user_id) REFERENCES users(id)
   );
*/
