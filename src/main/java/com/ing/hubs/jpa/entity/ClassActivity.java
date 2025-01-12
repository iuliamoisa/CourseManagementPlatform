package com.ing.hubs.jpa.entity;

import com.ing.hubs.jpa.entity.enums.AttendanceStatus;
import com.ing.hubs.jpa.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Class_Activity -> ID | schedule_id | user_id (stud) | status (present/absent) | date | grade | created_at

@Data
@Entity
@NoArgsConstructor
public class ClassActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "SCHEDULE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    @JoinColumn(name = "STUDENT_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "ATTENDANCE", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    private float grade;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
