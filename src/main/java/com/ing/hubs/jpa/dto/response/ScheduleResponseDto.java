package com.ing.hubs.jpa.dto.response;

import com.ing.hubs.jpa.entity.enums.CourseType;
import com.ing.hubs.jpa.entity.enums.WeekDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {
    private Long id;
    private CourseType courseType;
    private Long courseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private WeekDay weekDay;
    private LocalTime startTime;
    private LocalTime endTime;
}
