package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.EnrollmentRequestDto;
import com.ing.hubs.jpa.dto.request.EnrollmentUpdateDto;
import com.ing.hubs.jpa.dto.response.EnrollmentResponseDto;
import com.ing.hubs.jpa.entity.ClassActivity;
import com.ing.hubs.jpa.entity.Enrollment;
import com.ing.hubs.jpa.entity.Schedule;
import com.ing.hubs.jpa.entity.enums.EnrollmentStatus;
import com.ing.hubs.jpa.entity.enums.UserRole;
import com.ing.hubs.jpa.exception.class_activity.MissingGradesException;
import com.ing.hubs.jpa.exception.course.CourseNotFoundException;
import com.ing.hubs.jpa.exception.course.InvalidRoleException;
import com.ing.hubs.jpa.exception.enrollment.DuplicateEnrollmentException;
import com.ing.hubs.jpa.exception.enrollment.MaxAttendeesExceededException;
import com.ing.hubs.jpa.exception.enrollment.NotApprovedEnrollmentException;
import com.ing.hubs.jpa.exception.schedule.OverlappingCoursesException;
import com.ing.hubs.jpa.exception.user.UserNotFoundException;
import com.ing.hubs.jpa.repository.ClassActivityRepository;
import com.ing.hubs.jpa.repository.CourseRepository;
import com.ing.hubs.jpa.repository.EnrollmentRepository;
import com.ing.hubs.jpa.repository.UserRepository;
import com.ing.hubs.jpa.exception.enrollment.EnrollmentNotFoundException;
import com.ing.hubs.jpa.security.webToken.JwtService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EnrollmentService {
    private EnrollmentRepository enrollmentRepository;
    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private CourseRepository courseRepository;
    private ClassActivityRepository classActivityRepository;
    private ClassActivityService classActivityService;
    private final JwtService jwtService;

    public Enrollment loadEnrollment(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(EnrollmentNotFoundException::new);
    }

    public EnrollmentResponseDto getEnrollment(Long id) {
        var enrollment = loadEnrollment(id);
        return modelMapper.map(enrollment, EnrollmentResponseDto.class);
    }

    public List<EnrollmentResponseDto> getCurrentUserEnrollments(String jwtToken) {
        String emailFromJwt = jwtService.extractEmail(jwtToken);
        return enrollmentRepository.findAllByEmail(emailFromJwt).stream()
                .map(enrollment -> {
                    EnrollmentResponseDto responseDto = modelMapper.map(enrollment, EnrollmentResponseDto.class);
                    if (enrollment.getCourse() != null) {
                        responseDto.setCourseId(enrollment.getCourse().getId());
                    }
                    return responseDto;
                })
                .toList();
    }

    public EnrollmentResponseDto createEnrollment(EnrollmentRequestDto enrollmentRequestDto, String jwtToken) {
        String emailFromJwt = jwtService.extractEmail(jwtToken);
        var student = userRepository.findById(enrollmentRequestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Student"));
        if (student.getUserRole() != UserRole.STUDENT) {
            throw new InvalidRoleException("The user is not a student; they can't enroll in courses");
        } else if (!student.getEmail().equals(emailFromJwt)) {
            throw new NotApprovedEnrollmentException("You can't enroll in a course on behalf of other students");
        }
        var courseToAdd = courseRepository.findById(enrollmentRequestDto.getCourseId())
                .orElseThrow(CourseNotFoundException::new);

        enrollmentRepository.findByUserIdAndCourseId(student.getId(), courseToAdd.getId())
                .ifPresent(enrollment ->
                {
                    throw new DuplicateEnrollmentException("The student is already enrolled in this course");
                });

        for (Schedule schedule : courseToAdd.getScheduleList()) {
            var overlappingEnrollments = enrollmentRepository.findOverlappingEnrollments(
                    student.getId(),
                    schedule.getWeekDay(),
                    schedule.getStartTime(),
                    schedule.getEndTime()
            );
            if (!overlappingEnrollments.isEmpty()) {
                throw new OverlappingCoursesException("The student is already enrolled in another course at the same time");
            }
        }

        if (courseToAdd.getCurrentAttendees() + 1 > courseToAdd.getMaxAttendees()) {
            throw new MaxAttendeesExceededException();
        } else {
            courseToAdd.setCurrentAttendees(courseToAdd.getCurrentAttendees() + 1);
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(student);
        enrollment.setCourse(courseToAdd);
        enrollment.setStatus(EnrollmentStatus.PENDING);
        var savedEntity = enrollmentRepository.save(enrollment);
        return modelMapper.map(savedEntity, EnrollmentResponseDto.class);
    }

    public EnrollmentResponseDto updateEnrollment(Long id, EnrollmentUpdateDto enrollmentUpdateDto, String jwtToken) {
        String emailFromJwt = jwtService.extractEmail(jwtToken).toLowerCase();
        var enrollment = loadEnrollment(id);
        String profEmail = enrollment.getCourse().getUser().getEmail().toLowerCase();
        if (!emailFromJwt.equals(profEmail)) {
            throw new NotApprovedEnrollmentException("The user must be the professor that teaches the course");
        }
        if (enrollmentUpdateDto.getStatus() != null) {
            enrollment.setStatus(enrollmentUpdateDto.getStatus());
        }
        if (enrollmentUpdateDto.getGpa() != null) {
            enrollment.setGpa(enrollmentUpdateDto.getGpa());
        }
        enrollment.setUpdatedAt(enrollmentUpdateDto.getUpdatedAt());
        var updatedEnrollment = enrollmentRepository.save(enrollment);
        return modelMapper.map(updatedEnrollment, EnrollmentResponseDto.class);
    }

    public void deleteById(Long id, String jwtToken) {
        String emailFromJwt = jwtService.extractEmail(jwtToken);
        var enrollment = loadEnrollment(id);
        if (!enrollment.getUser().getEmail().equals(emailFromJwt)) {
            throw new NotApprovedEnrollmentException("You can't delete an enrollment on behalf of other students");
        }
        enrollmentRepository.deleteById(id);
    }

    public List<EnrollmentResponseDto> getAllEnrollmentsByCourse(String jwtToken, long courseID) {
        if(courseRepository.findById(courseID).isEmpty()){
            throw new CourseNotFoundException();
        }
        if (!jwtService.extractEmail(jwtToken).equals(courseRepository.findById(courseID).get().getUser().getEmail())) {
            throw new NotApprovedEnrollmentException("You can't check enrollments for a course taught by other professor");
        }
        return enrollmentRepository.findAllByCourseId(courseID).stream()
                .map(enrollment -> {
                    EnrollmentResponseDto responseDto = modelMapper.map(enrollment, EnrollmentResponseDto.class);
                    if (enrollment.getUser() != null) {
                        responseDto.setUserId(enrollment.getUser().getId());
                    }
                    return responseDto;
                })
                .toList();
    }

    public List<EnrollmentResponseDto> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(enrollment -> modelMapper.map(enrollment, EnrollmentResponseDto.class))
                .toList();
    }

    public List<EnrollmentResponseDto> getAllEnrollmentsByUserId(Long id) {
        return enrollmentRepository.findAllByUserId(id).stream()
                .map(enrollment -> {
                    EnrollmentResponseDto responseDto = modelMapper.map(enrollment, EnrollmentResponseDto.class);
                    if (enrollment.getCourse() != null) {
                        responseDto.setCourseId(enrollment.getCourse().getId());
                    }
                    return responseDto;
                })

                .toList();
    }

    public EnrollmentResponseDto setGpa(Long enrollmentId, String jwtToken) {
        String emailFromJwt = jwtService.extractEmail(jwtToken);
        var professor = userRepository.findByEmail(emailFromJwt)
                .orElseThrow(() -> new UserNotFoundException("Professor"));

        var enrollment = loadEnrollment(enrollmentId);

        if (enrollment.getStatus() != EnrollmentStatus.APPROVED) {
            throw new NotApprovedEnrollmentException("GPA can only be set for enrollments with status 'APPROVED'.");
        }

        List<ClassActivity> labActivities = classActivityRepository.findLabsByScheduleCourseIdAndUserId(
                enrollment.getCourse().getId(),
                enrollment.getUser().getId());

        System.out.println("labActivities: " + labActivities);
        List<ClassActivity> courseActivities = classActivityRepository.findLCoursesByScheduleCourseIdAndUserId(
                enrollment.getCourse().getId(),
                enrollment.getUser().getId());

        System.out.println("courseActivities: " + courseActivities);

        double labAverage = labActivities.stream()
                .mapToDouble(ClassActivity::getGrade)
                .filter(grade -> grade > 0)
                .average()
                .orElseThrow(() -> new MissingGradesException("Lab grades are missing."));

        double courseGrade = courseActivities.stream()
                .mapToDouble(ClassActivity::getGrade)
                .filter(grade -> grade > 0)
                .average()
                .orElseThrow(() -> new MissingGradesException("Course grades are missing."));

        double gpa = (labAverage + courseGrade) / 2;

        System.out.println("gpa: " + gpa);
        enrollment.setGpa(gpa);
        enrollmentRepository.save(enrollment);

        return modelMapper.map(enrollment, EnrollmentResponseDto.class);
    }
}
