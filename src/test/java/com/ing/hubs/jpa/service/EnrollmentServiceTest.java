package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.EnrollmentRequestDto;
import com.ing.hubs.jpa.dto.request.EnrollmentUpdateDto;
import com.ing.hubs.jpa.dto.response.EnrollmentResponseDto;
import com.ing.hubs.jpa.entity.*;
import com.ing.hubs.jpa.entity.enums.EnrollmentStatus;
import com.ing.hubs.jpa.entity.enums.UserRole;
import com.ing.hubs.jpa.exception.enrollment.*;
import com.ing.hubs.jpa.exception.class_activity.MissingGradesException;
import com.ing.hubs.jpa.repository.ClassActivityRepository;
import com.ing.hubs.jpa.repository.CourseRepository;
import com.ing.hubs.jpa.repository.EnrollmentRepository;
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
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ClassActivityRepository classActivityRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Enrollment enrollment;
    private EnrollmentRequestDto enrollmentRequestDto;
    private EnrollmentUpdateDto enrollmentUpdateDto;
    private User student;
    private Course course;
    private EnrollmentResponseDto enrollmentResponseDto;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setEmail("test@student.com");
        student.setUserRole(UserRole.STUDENT);

        course = new Course();
        course.setId(1L);
        course.setName("Test Course");
        course.setMaxAttendees(10);
        course.setCurrentAttendees(0);

        enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setUser(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDING);

        enrollmentRequestDto = new EnrollmentRequestDto();
        enrollmentRequestDto.setUserId(1L);
        enrollmentRequestDto.setCourseId(1L);

        enrollmentUpdateDto = new EnrollmentUpdateDto();
        enrollmentUpdateDto.setStatus(EnrollmentStatus.APPROVED);

        enrollmentResponseDto = new EnrollmentResponseDto();
    }

    @Test
    void shouldLoadEnrollmentWhenIdIsValid() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        Enrollment result = enrollmentService.loadEnrollment(1L);
        assertNotNull(result);
        assertEquals(enrollment, result);
        verify(enrollmentRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EnrollmentNotFoundException.class, () -> enrollmentService.loadEnrollment(1L));
    }

    @Test
    void shouldReturnEnrollmentResponseWhenIdIsValid() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(modelMapper.map(enrollment, EnrollmentResponseDto.class)).thenReturn(enrollmentResponseDto);

        EnrollmentResponseDto result = enrollmentService.getEnrollment(1L);
        assertNotNull(result);
        verify(enrollmentRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEnrollmentsForCurrentUser() {
        when(jwtService.extractEmail("token")).thenReturn("test@student.com");
        when(enrollmentRepository.findAllByEmail("test@student.com")).thenReturn(List.of(enrollment));
        when(modelMapper.map(enrollment, EnrollmentResponseDto.class)).thenReturn(enrollmentResponseDto);

        List<EnrollmentResponseDto> results = enrollmentService.getCurrentUserEnrollments("token");
        assertEquals(1, results.size());
        verify(enrollmentRepository, times(1)).findAllByEmail("test@student.com");
    }

    @Test
    void shouldCreateEnrollmentSuccessfully() {
        when(jwtService.extractEmail("token")).thenReturn("test@student.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
        when(modelMapper.map(enrollment, EnrollmentResponseDto.class)).thenReturn(enrollmentResponseDto);

        EnrollmentResponseDto result = enrollmentService.createEnrollment(enrollmentRequestDto, "token");
        assertNotNull(result);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void shouldThrowExceptionForDuplicateEnrollment() {
        when(jwtService.extractEmail("token")).thenReturn("test@student.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));

        assertThrows(DuplicateEnrollmentException.class,
                () -> enrollmentService.createEnrollment(enrollmentRequestDto, "token"));
    }

    @Test
    void shouldUpdateEnrollmentStatus() {
        String professorEmail = "professor@example.com";
        String jwtToken = "token";
        User professor = new User();
        professor.setEmail(professorEmail);
        Course course = new Course();
        course.setUser(professor);
        enrollment.setCourse(course);
        when(jwtService.extractEmail(jwtToken)).thenReturn(professorEmail);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        when(modelMapper.map(enrollment, EnrollmentResponseDto.class)).thenReturn(enrollmentResponseDto);
        EnrollmentResponseDto result = enrollmentService.updateEnrollment(1L, enrollmentUpdateDto, jwtToken);
        assertNotNull(result);
        assertEquals(EnrollmentStatus.APPROVED, enrollment.getStatus());
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    void shouldDeleteEnrollmentById() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(jwtService.extractEmail("token")).thenReturn("test@student.com");

        enrollmentService.deleteById(1L, "token");
        verify(enrollmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingEnrollmentForOtherUser() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(jwtService.extractEmail("token")).thenReturn("another@student.com");

        assertThrows(RuntimeException.class, () -> enrollmentService.deleteById(1L, "token"));
    }

    @Test
    void shouldThrowExceptionWhenGradesMissingForGpa() {
        String jwtToken = "token";
        String professorEmail = "professor@test.com";
        when(jwtService.extractEmail(jwtToken)).thenReturn(professorEmail);
        User professor = new User();
        professor.setEmail(professorEmail);
        when(userRepository.findByEmail(professorEmail)).thenReturn(Optional.of(professor));
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStatus(EnrollmentStatus.APPROVED);
        Course course = new Course();
        course.setId(1L);
        enrollment.setCourse(course);
        User student = new User();
        student.setId(1L);
        enrollment.setUser(student);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(classActivityRepository.findLabsByScheduleCourseIdAndUserId(1L, 1L))
                .thenReturn(List.of());
        ClassActivity lectureActivity = new ClassActivity();
        lectureActivity.setGrade(9f);
        when(classActivityRepository.findLCoursesByScheduleCourseIdAndUserId(1L, 1L))
                .thenReturn(List.of(lectureActivity));
        assertThrows(MissingGradesException.class, () -> enrollmentService.setGpa(1L, jwtToken));
    }
}
