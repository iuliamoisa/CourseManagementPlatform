package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.ScheduleRequestDto;
import com.ing.hubs.jpa.dto.request.ScheduleUpdateDto;
import com.ing.hubs.jpa.dto.response.ScheduleResponseDto;
import com.ing.hubs.jpa.entity.Course;
import com.ing.hubs.jpa.entity.Schedule;
import com.ing.hubs.jpa.entity.User;
import com.ing.hubs.jpa.entity.enums.WeekDay;
import com.ing.hubs.jpa.exception.course.CourseNotFoundException;
import com.ing.hubs.jpa.exception.schedule.*;
import com.ing.hubs.jpa.repository.CourseRepository;
import com.ing.hubs.jpa.repository.ScheduleRepository;
import com.ing.hubs.jpa.security.webToken.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ScheduleService scheduleService;

    private ScheduleRequestDto scheduleRequestDto;
    private Schedule schedule;
    private Course course;
    private User prof;

    @BeforeEach
    void beforeEach() {
        prof = new User();
        prof.setId(1L);
        prof.setEmail("prof@example.com");

        course = new Course();
        course.setId(1L);
        course.setName("Bio");
        course.setUser(prof);

        scheduleRequestDto = new ScheduleRequestDto();
        scheduleRequestDto.setCourseId(1L);
        scheduleRequestDto.setStartDate(LocalDate.parse("2025-01-10"));
        scheduleRequestDto.setEndDate(LocalDate.parse("2025-01-15"));
        scheduleRequestDto.setWeekDay(WeekDay.FRIDAY);
        scheduleRequestDto.setStartTime(LocalTime.parse("10:00"));
        scheduleRequestDto.setEndTime(LocalTime.parse("11:30"));

        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setCourse(course);
        schedule.setStartDate(scheduleRequestDto.getStartDate());
        schedule.setEndDate(scheduleRequestDto.getEndDate());
        schedule.setWeekDay(scheduleRequestDto.getWeekDay());
        schedule.setStartTime(scheduleRequestDto.getStartTime());
        schedule.setEndTime(scheduleRequestDto.getEndTime());
    }

    @Test
    void shouldGetAllSchedules() {
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));
        when(modelMapper.map(schedule, ScheduleResponseDto.class)).thenReturn(new ScheduleResponseDto());
        var response = scheduleService.getAllSchedules();
        assertNotNull(response, "Response should not be null");
        assertEquals(1, response.size(), "Response list should contain one schedule");
        verify(scheduleRepository).findAll();
    }

    @Test
    void shouldCreateSchedule() {
        when(jwtService.extractEmail("token")).thenReturn("prof@example.com");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        when(modelMapper.map(scheduleRequestDto, Schedule.class)).thenReturn(schedule);
        when(modelMapper.map(schedule, ScheduleResponseDto.class)).thenReturn(new ScheduleResponseDto());
        var response = scheduleService.createSchedule(scheduleRequestDto, "token");
        assertNotNull(response, "Response should not be null");
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class, () -> scheduleService.createSchedule(scheduleRequestDto, "token"));
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void shouldDeleteSchedule() {
        doNothing().when(scheduleRepository).deleteById(1L);
        scheduleService.deleteSchedule(1L);
        verify(scheduleRepository).deleteById(1L);
    }

    @Test
    void shouldLoadSchedule() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        var result = scheduleService.loadSchedule(1L);
        assertNotNull(result, "Loaded schedule should not be null");
        assertEquals(schedule, result, "Loaded schedule should match the expected schedule");
    }

    @Test
    void shouldThrowExceptionWhenScheduleNotFound() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.loadSchedule(1L));
    }

    @Test
    void shouldUpdateSchedule() {
        ScheduleUpdateDto updateDto = new ScheduleUpdateDto();
        updateDto.setStartDate("2025-02-01");
        updateDto.setEndDate("2025-02-05");
        updateDto.setWeekDay("WEDNESDAY");
        updateDto.setStartTime("09:00");
        updateDto.setEndTime("10:30");
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        when(modelMapper.map(schedule, ScheduleResponseDto.class)).thenReturn(new ScheduleResponseDto());
        var response = scheduleService.updateSchedule(1L, updateDto);
        assertNotNull(response, "Response should not be null");
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void shouldThrowExceptionForInvalidDates() {
        schedule.setStartDate(LocalDate.now().minusDays(1));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        assertThrows(InvalidDateException.class, () -> scheduleService.updateSchedule(1L, new ScheduleUpdateDto()));
    }

    @Test
    void shouldThrowExceptionForInvalidTimes() {
        schedule.setStartTime(LocalTime.of(14, 0));
        schedule.setEndTime(LocalTime.of(13, 0));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        assertThrows(InvalidTimeException.class, () -> scheduleService.updateSchedule(1L, new ScheduleUpdateDto()));
    }

    @Test
    void shouldThrowExceptionForOverlappingSchedules() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.findOverlappingSchedules(anyString(), any(WeekDay.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(List.of(schedule));
        assertThrows(OverlappingCoursesException.class, () -> scheduleService.updateSchedule(1L, new ScheduleUpdateDto()));
    }

    @Test
    void shouldGetSchedulesByCourse() {
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(scheduleRepository.findAllByCourseId(1L)).thenReturn(List.of(schedule));
        when(modelMapper.map(schedule, ScheduleResponseDto.class)).thenReturn(new ScheduleResponseDto());
        var response = scheduleService.getSchedulesByCourse(1L);
        assertNotNull(response, "Response should not be null");
        assertEquals(1, response.size(), "Response list should contain one schedule");
        verify(scheduleRepository).findAllByCourseId(1L);
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFoundForSchedules() {
        when(courseRepository.existsById(1L)).thenReturn(false);
        assertThrows(CourseNotFoundException.class, () -> scheduleService.getSchedulesByCourse(1L));
    }
}
