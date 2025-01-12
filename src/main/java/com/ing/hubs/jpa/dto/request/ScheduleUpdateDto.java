package com.ing.hubs.jpa.dto.request;

import com.ing.hubs.jpa.entity.enums.CourseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScheduleUpdateDto {
    private CourseType courseType;
    private String startDate;
    private String endDate;
    private String weekDay;
    private String startTime;
    private String endTime;
}
