package com.ing.hubs.jpa.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseUpdateDto {
    private String name;
    private String description;
    @Min(value = 1, message = "Number of possible attendees must be greater than 0")
    @Max(value = 40, message = "Number of possible attendees must not exceed 40")
    private int maxAttendees;
}
