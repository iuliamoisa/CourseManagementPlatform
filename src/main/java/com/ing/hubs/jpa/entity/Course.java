package com.ing.hubs.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Course -> ID | name | description | max_attendees | user_id (constraint : prof) | created_at | updated_at
@Data
@Entity
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Course(Long id) {
        this.id = id;
    }

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "MAX_ATTENDEES", nullable = false)
    private int maxAttendees;

    @Column(name = "CURRENT_ATTENDEES", nullable = false)
    private int currentAttendees = 0;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFESSOR_ID", nullable = false)
    private User user;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();


    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    private List<Enrollment> enrollments = new ArrayList<>();


    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course") //???
    private List<Schedule> scheduleList = new ArrayList<>();
}
