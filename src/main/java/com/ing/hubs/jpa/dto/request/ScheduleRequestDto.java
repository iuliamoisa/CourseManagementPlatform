package com.ing.hubs.jpa.dto.request;

import com.ing.hubs.jpa.entity.enums.CourseType;
import com.ing.hubs.jpa.entity.enums.WeekDay;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestDto {

    @NotNull(message = "Course type must not be null")
    private CourseType courseType;

    @NotNull(message = "Course id must not be null")
    private Long courseId;

    @NotNull(message = "Start date must not be null")
    @FutureOrPresent(message = "Start date must be in the future or present")
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull(message = "Start time must not be null")
    private WeekDay weekDay;

    @NotNull(message = "Start time must not be null")
    private LocalTime startTime;

    @NotNull(message = "Start time must not be null")
    private LocalTime endTime;
}
