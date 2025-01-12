package com.ing.hubs.jpa.dto.request;

import com.ing.hubs.jpa.entity.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnrollmentUpdateDto {
    private EnrollmentStatus status;
    private Double gpa;
    private LocalDateTime updatedAt = LocalDateTime.now();
}
