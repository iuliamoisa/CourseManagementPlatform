package com.ing.hubs.jpa.entity;

// ID | user_id (constraint : stud) | course_id | status (Enum : approved, pending, denied) |
// GPA | updated_at | created_at

import com.ing.hubs.jpa.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "STUDENT_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "COURSE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private Double gpa;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}
