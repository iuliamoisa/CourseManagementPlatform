package com.ing.hubs.jpa.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequestDto {
    @NotNull(message = "Course name must not be null")
    private String name;

    @NotNull(message = "Description must not be null")
    private String description;

    @NotNull(message = "Number of possible attendees attendees must not be null")
    @Min(value = 1, message = "Number of possible attendees must be greater than 0")
    @Max(value = 40, message = "Number of possible attendees must not exceed 40")
    private int maxAttendees;

    @NotNull(message = "Professor ID must not be null")
    private Long professorId;
}
