package com.ing.hubs.jpa.dto.request;

import com.ing.hubs.jpa.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassActivityRequestDto {

    @NotNull(message = "Schedule ID must not be null")
    @Positive(message = "Schedule ID must be a positive number")
    private Long scheduleId;

    @NotNull(message = "User ID must not be null")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @NotNull(message = "Enrollment status must not be null")
    private AttendanceStatus attendanceStatus;

    @Min(value = 1, message = "Grade must be at least 1")
    @Max(value = 10, message = "Grade must not exceed 10")
    private float grade;

    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();
}
