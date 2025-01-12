package com.ing.hubs.jpa.dto.request;

import com.ing.hubs.jpa.entity.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentRequestDto {
    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Course ID must not be null")
    private Long courseId;

    private EnrollmentStatus status = EnrollmentStatus.PENDING;
    //private Double gpa = 0.0;
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();
}
