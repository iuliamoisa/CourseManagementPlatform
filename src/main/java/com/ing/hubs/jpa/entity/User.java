package com.ing.hubs.jpa.entity;

import com.ing.hubs.jpa.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "USER_ROLE", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user") //???
    private List<Course> courses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Enrollment> enrollments = new ArrayList<>();
}
