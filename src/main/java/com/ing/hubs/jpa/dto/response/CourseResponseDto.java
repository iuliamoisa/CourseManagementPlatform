package com.ing.hubs.jpa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDto {
    private Long id;
    private String name;
    private String description;
    private int currentAttendees;
    private int maxAttendees;
    private Long professorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
