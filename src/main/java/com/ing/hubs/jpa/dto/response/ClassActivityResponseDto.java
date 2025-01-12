package com.ing.hubs.jpa.dto.response;

import com.ing.hubs.jpa.entity.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassActivityResponseDto {
    private Long id;
    private Long scheduleId;
    private Long userId;
    private AttendanceStatus attendanceStatus;
    private float grade;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
