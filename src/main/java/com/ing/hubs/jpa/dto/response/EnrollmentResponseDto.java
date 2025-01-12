package com.ing.hubs.jpa.dto.response;

import com.ing.hubs.jpa.entity.Course;
import com.ing.hubs.jpa.entity.User;
import com.ing.hubs.jpa.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponseDto {
    private Long id;
    private Long userId;
    private Long courseId;
    private EnrollmentStatus status;
    private Double gpa;
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();
}
