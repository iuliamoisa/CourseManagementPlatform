package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.CourseRequestDto;
import com.ing.hubs.jpa.dto.request.CourseUpdateDto;
import com.ing.hubs.jpa.dto.response.CourseResponseDto;
import com.ing.hubs.jpa.entity.Course;
import com.ing.hubs.jpa.entity.User;
import com.ing.hubs.jpa.entity.enums.UserRole;
import com.ing.hubs.jpa.exception.course.CourseNotFoundException;
import com.ing.hubs.jpa.exception.course.InvalidRoleException;
import com.ing.hubs.jpa.exception.course.ProfessorNotFoundException;
import com.ing.hubs.jpa.repository.CourseRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CourseService courseService;

    private CourseRequestDto courseRequestDto;
    private Course course;
    private User professor;

    @BeforeEach
    void beforeEach() {
        courseRequestDto = new CourseRequestDto();
        courseRequestDto.setName("Matematica");
        courseRequestDto.setDescription("O materie grozava");
        courseRequestDto.setMaxAttendees(30);
        courseRequestDto.setProfessorId(1L);

        professor = new User();
        professor.setId(1L);
        professor.setEmail("gigiduru@gmail.com");
        professor.setUserRole(UserRole.PROFESSOR);

        course = new Course();
        course.setId(1L);
        course.setName(courseRequestDto.getName());
        course.setDescription(courseRequestDto.getDescription());
        course.setMaxAttendees(courseRequestDto.getMaxAttendees());
        course.setUser(professor);
    }

    @Test
    void shouldCreateCourse() {
        when(userRepository.findById(courseRequestDto.getProfessorId())).thenReturn(Optional.of(professor));
        when(jwtService.extractEmail(anyString())).thenReturn(professor.getEmail());
        when(modelMapper.map(courseRequestDto, Course.class)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(modelMapper.map(any(Course.class), eq(CourseResponseDto.class))).thenReturn(new CourseResponseDto());
        var response = courseService.createCourse("validJwt", courseRequestDto);
        assertNotNull(response, "Response should not be null");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void shouldThrowExceptionWhenProfessorNotFound() {
        when(userRepository.findById(courseRequestDto.getProfessorId())).thenReturn(Optional.empty());
        assertThrows(ProfessorNotFoundException.class, () -> courseService.createCourse("validJwt", courseRequestDto));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void shouldThrowExceptionForInvalidProfessorRole() {
        professor.setUserRole(UserRole.STUDENT);
        when(userRepository.findById(courseRequestDto.getProfessorId())).thenReturn(Optional.of(professor));
        assertThrows(InvalidRoleException.class, () -> courseService.createCourse("validJwt", courseRequestDto));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void shouldThrowExceptionForUnauthorizedJwt() {
        when(userRepository.findById(courseRequestDto.getProfessorId())).thenReturn(Optional.of(professor));
        when(jwtService.extractEmail(anyString())).thenReturn("ceva@example.com");
        assertThrows(RuntimeException.class, () -> courseService.createCourse("invalidJwt", courseRequestDto));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void shouldGetAllCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(modelMapper.map(course, CourseResponseDto.class)).thenReturn(new CourseResponseDto());
        List<CourseResponseDto> courses = courseService.getAllCourses();
        assertEquals(1, courses.size(), "List should contain one course");
        verify(modelMapper).map(course, CourseResponseDto.class);
    }

    @Test
    void shouldGetCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(modelMapper.map(course, CourseResponseDto.class)).thenReturn(new CourseResponseDto());
        var response = courseService.getCourse(1L);
        assertNotNull(response, "Response should not be null");
        verify(modelMapper).map(course, CourseResponseDto.class);
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class, () -> courseService.getCourse(1L));
    }

    @Test
    void shouldUpdateCourse() {
        CourseUpdateDto updateDto = new CourseUpdateDto();
        updateDto.setName("Mate Avansata");
        updateDto.setDescription("Imi pare rau daca faci cursul asta...");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(jwtService.extractEmail(anyString())).thenReturn(professor.getEmail());
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(modelMapper.map(course, CourseResponseDto.class)).thenReturn(new CourseResponseDto());
        var response = courseService.updateCourse("validJwt", 1L, updateDto);
        assertNotNull(response, "Updated course should not be null");
        verify(courseRepository).save(course);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingOtherProfessorCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(jwtService.extractEmail(anyString())).thenReturn("ceva@example.com");
        assertThrows(RuntimeException.class, () -> courseService.updateCourse("invalidJwt", 1L, new CourseUpdateDto()));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void shouldDeleteCourse() {
        doNothing().when(courseRepository).deleteById(1L);
        courseService.delete(1L);
        verify(courseRepository).deleteById(1L);
    }
}
