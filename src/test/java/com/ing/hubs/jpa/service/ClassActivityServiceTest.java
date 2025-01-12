package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.ClassActivityRequestDto;
import com.ing.hubs.jpa.dto.request.ClassActivityUpdateDto;
import com.ing.hubs.jpa.dto.response.ClassActivityResponseDto;
import com.ing.hubs.jpa.entity.ClassActivity;
import com.ing.hubs.jpa.entity.Schedule;
import com.ing.hubs.jpa.entity.User;
import com.ing.hubs.jpa.entity.enums.AttendanceStatus;
import com.ing.hubs.jpa.entity.enums.UserRole;
import com.ing.hubs.jpa.exception.class_activity.ClassActivityNotFoundException;
import com.ing.hubs.jpa.exception.class_activity.DuplicateActivityException;
import com.ing.hubs.jpa.exception.class_activity.InvalidGradeException;
import com.ing.hubs.jpa.exception.course.InvalidRoleException;
import com.ing.hubs.jpa.exception.schedule.ScheduleNotFoundException;
import com.ing.hubs.jpa.exception.user.UserNotFoundException;
import com.ing.hubs.jpa.repository.ClassActivityRepository;
import com.ing.hubs.jpa.repository.ScheduleRepository;
import com.ing.hubs.jpa.repository.UserRepository;
import com.ing.hubs.jpa.security.webToken.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassActivityServiceTest {
    @Mock
    private ClassActivityRepository classActivityRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ClassActivityService classActivityService;

    private ClassActivityRequestDto activityRequestDto;
    private ClassActivity classActivity;
    private Schedule schedule;
    private User student;

    @BeforeEach
    void beforeEach() {
        activityRequestDto = new ClassActivityRequestDto();
        activityRequestDto.setGrade(9.0f);
        activityRequestDto.setAttendanceStatus(AttendanceStatus.PRESENT);
        activityRequestDto.setScheduleId(1L);
        activityRequestDto.setUserId(1L);

        student = new User();
        student.setId(1L);
        student.setEmail("student@gmail.com");
        student.setUserRole(UserRole.STUDENT);

        schedule = new Schedule();
        schedule.setId(1L);

        classActivity = new ClassActivity();
        classActivity.setId(1L);
        classActivity.setUser(student);
        classActivity.setSchedule(schedule);
        classActivity.setGrade(activityRequestDto.getGrade());
        classActivity.setAttendanceStatus(activityRequestDto.getAttendanceStatus());
    }

    @Test
    void shouldLoadActivity() {
        when(classActivityRepository.findById(1L)).thenReturn(Optional.of(classActivity));
        ClassActivity loadedActivity = classActivityService.loadActivity(1L);
        assertEquals(classActivity, loadedActivity, "Loaded activity should be equal to the expected activity");
    }

    @Test
    void shouldThrowExceptionWhenActivityNotFound() {
        when(classActivityRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassActivityNotFoundException.class, () -> classActivityService.loadActivity(1L));
    }

    @Test
    void shouldGetActivityById() {
        when(classActivityRepository.findById(1L)).thenReturn(Optional.of(classActivity));
        when(modelMapper.map(classActivity, ClassActivityResponseDto.class)).thenReturn(new ClassActivityResponseDto());
        var response = classActivityService.getActivity(1L);
        assertNotNull(response, "Response should not be null");
        verify(modelMapper).map(classActivity, ClassActivityResponseDto.class);
    }

    @Test
    void shouldCreateActivity() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(classActivityRepository.save(any(ClassActivity.class))).thenReturn(classActivity);
        when(modelMapper.map(classActivity, ClassActivityResponseDto.class)).thenReturn(new ClassActivityResponseDto());
        var response = classActivityService.createActivity(activityRequestDto);
        assertNotNull(response, "Response should not be null");
        verify(classActivityRepository).save(any(ClassActivity.class));
    }

    @Test
    void shouldThrowExceptionWhenScheduleNotFound() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClassActivityNotFoundException.class, () -> classActivityService.createActivity(activityRequestDto));
        verify(classActivityRepository, never()).save(any(ClassActivity.class));
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFound() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> classActivityService.createActivity(activityRequestDto));
        verify(classActivityRepository, never()).save(any(ClassActivity.class));
    }

    @Test
    void shouldThrowExceptionForInvalidRole() {
        student.setUserRole(UserRole.PROFESSOR);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        assertThrows(InvalidRoleException.class, () -> classActivityService.createActivity(activityRequestDto));
        verify(classActivityRepository, never()).save(any(ClassActivity.class));
    }

    @Test
    void shouldUpdateActivity() {
        ClassActivityUpdateDto updateDto = new ClassActivityUpdateDto();
        updateDto.setGrade(8.5f);
        updateDto.setAttendanceStatus(AttendanceStatus.ABSENT);
        when(classActivityRepository.findById(1L)).thenReturn(Optional.of(classActivity));
        when(classActivityRepository.save(any(ClassActivity.class))).thenReturn(classActivity);
        when(modelMapper.map(classActivity, ClassActivityResponseDto.class)).thenReturn(new ClassActivityResponseDto());
        var response = classActivityService.updateActivity(1L, updateDto);
        assertNotNull(response, "Updated activity should not be null");
        verify(classActivityRepository).save(classActivity);
    }

    @Test
    void shouldThrowExceptionForInvalidGrade() {
        ClassActivityUpdateDto updateDto = new ClassActivityUpdateDto();
        updateDto.setGrade(12.0f);
        when(classActivityRepository.findById(1L)).thenReturn(Optional.of(classActivity));
        assertThrows(InvalidGradeException.class, () -> classActivityService.updateActivity(1L, updateDto));
        verify(classActivityRepository, never()).save(any(ClassActivity.class));
    }

    @Test
    void shouldDeleteActivity() {
        doNothing().when(classActivityRepository).deleteById(1L);
        classActivityService.deleteActivity(1L);
        verify(classActivityRepository).deleteById(1L);
    }

    @Test
    void shouldGetUserActivitiesForCourse() {
        when(jwtService.extractEmail(anyString())).thenReturn(student.getEmail());
        when(userRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(scheduleRepository.findAllByCourseId(1L)).thenReturn(List.of(schedule));
        when(classActivityRepository.findAllByUserIdAndScheduleIdIn(eq(1L), anyList()))
                .thenReturn(List.of(classActivity));
        when(modelMapper.map(classActivity, ClassActivityResponseDto.class)).thenReturn(new ClassActivityResponseDto());
        var response = classActivityService.getUserActivitiesForCourse("validJwt", 1L);
        assertNotNull(response, "Response should not be null");
        assertEquals(1, response.size(), "Response list should contain one activity");
        verify(classActivityRepository).findAllByUserIdAndScheduleIdIn(eq(1L), anyList());
    }

    @Test
    void shouldThrowExceptionWhenSchedulesNotFound() {
        when(jwtService.extractEmail(anyString())).thenReturn(student.getEmail());
        when(userRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(scheduleRepository.findAllByCourseId(1L)).thenReturn(List.of());
        assertThrows(ScheduleNotFoundException.class,
                () -> classActivityService.getUserActivitiesForCourse("validJwt", 1L));
    }
}
