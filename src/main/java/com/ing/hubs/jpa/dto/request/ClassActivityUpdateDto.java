package com.ing.hubs.jpa.dto.request;

import com.ing.hubs.jpa.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassActivityUpdateDto {

    private AttendanceStatus attendanceStatus;

    @Min(value = 1, message = "Grade must be at least 1")
    @Max(value = 10, message = "Grade must not exceed 10")
    private float grade;

    private LocalDateTime updatedAt = LocalDateTime.now();
}
