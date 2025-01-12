package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.ScheduleRequestDto;
import com.ing.hubs.jpa.dto.request.ScheduleUpdateDto;
import com.ing.hubs.jpa.dto.response.ScheduleResponseDto;
import com.ing.hubs.jpa.entity.Schedule;
import com.ing.hubs.jpa.entity.enums.WeekDay;
import com.ing.hubs.jpa.exception.schedule.*;
import com.ing.hubs.jpa.exception.course.CourseNotFoundException;
import com.ing.hubs.jpa.repository.CourseRepository;
import com.ing.hubs.jpa.repository.ScheduleRepository;
import com.ing.hubs.jpa.security.webToken.JwtService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService {
    private ScheduleRepository scheduleRepository;
    private ModelMapper modelMapper;
    private CourseRepository courseRepository;
    private JwtService jwtService;

    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleResponseDto.class))
                .collect(Collectors.toList());
    }

    public ScheduleResponseDto createSchedule(ScheduleRequestDto scheduleRequestDto, String jwtToken) {
        var course = courseRepository.findById(scheduleRequestDto.getCourseId())
                .orElseThrow(CourseNotFoundException::new);
        // prof can establish a schedule for their course
        String emailFromJwt = jwtService.extractEmail(jwtToken);
        if (!course.getUser().getEmail().equals(emailFromJwt)) {
            throw new CourseDoesNotBelongToProfException();
        }
        Schedule schedule = modelMapper.map(scheduleRequestDto, Schedule.class);
        schedule.setId(null);
        schedule.setCourse(course);
        validateSchedule(schedule);
        Schedule savedEntity = scheduleRepository.save(schedule);
        return modelMapper.map(savedEntity, ScheduleResponseDto.class);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public Schedule loadSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(ScheduleNotFoundException::new);
    }

    public ScheduleResponseDto updateSchedule(Long id, ScheduleUpdateDto scheduleUpdateDto) {
        Schedule schedule = loadSchedule(id);
        if(scheduleUpdateDto.getCourseType() != null){
            schedule.setCourseType(scheduleUpdateDto.getCourseType());
        }
        if(scheduleUpdateDto.getStartDate() != null){
            schedule.setStartDate(LocalDate.parse(scheduleUpdateDto.getStartDate()));
        }
        if(scheduleUpdateDto.getEndDate() != null){
            schedule.setEndDate(LocalDate.parse(scheduleUpdateDto.getEndDate()));
        }

        if (scheduleUpdateDto.getWeekDay() != null) {
            schedule.setWeekDay(WeekDay.valueOf(scheduleUpdateDto.getWeekDay()));
        }

        if (scheduleUpdateDto.getStartTime() != null) {
            schedule.setStartTime(LocalTime.parse(scheduleUpdateDto.getStartTime()));
        }
        if (scheduleUpdateDto.getEndTime() != null) {
            schedule.setEndTime(LocalTime.parse(scheduleUpdateDto.getEndTime()));
        }

        validateSchedule(schedule);

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return modelMapper.map(updatedSchedule, ScheduleResponseDto.class);
    }

    public List<ScheduleResponseDto> getSchedulesByCourse(Long id) {
        var courseExists = courseRepository.existsById(id);
        if (!courseExists) {
            throw new CourseNotFoundException();
        }

        return scheduleRepository.findAllByCourseId(id).stream()
                .map(schedule -> {
                    ScheduleResponseDto responseDto = modelMapper.map(schedule, ScheduleResponseDto.class);
                    if (schedule.getCourse() != null) {
                        responseDto.setCourseId(schedule.getCourse().getId());
                    }
                    return responseDto;
                })
                .toList();
    }

    private void validateSchedule(Schedule schedule) {
        if (schedule.getStartDate().isBefore(LocalDate.now())
                || schedule.getEndDate().isBefore(LocalDate.now())
                || schedule.getStartDate().isAfter(schedule.getEndDate())) {
            throw new InvalidDateException();
        }

        if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
            if (schedule.getStartTime().isAfter(schedule.getEndTime())) {
                throw new InvalidTimeException();
            }
            long durationInMinutes = schedule.getStartTime().until(schedule.getEndTime(), java.time.temporal.ChronoUnit.MINUTES);
            if (durationInMinutes > 120) {
                throw new InvalidCourseDurationException();
            }
        }

        List<Schedule> overlappingSchedules = scheduleRepository.findOverlappingSchedules(
                schedule.getCourse().getName(),
                schedule.getWeekDay(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );

        if (!overlappingSchedules.isEmpty()) {
            throw new OverlappingCoursesException("Courses cannot take place in the same time frame");
        }
    }

}
