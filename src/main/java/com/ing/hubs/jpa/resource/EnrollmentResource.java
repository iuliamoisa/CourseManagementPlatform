package com.ing.hubs.jpa.resource;

import com.ing.hubs.jpa.dto.request.EnrollmentRequestDto;
import com.ing.hubs.jpa.dto.request.EnrollmentUpdateDto;
import com.ing.hubs.jpa.dto.response.EnrollmentResponseDto;
import com.ing.hubs.jpa.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/enrollments")
public class EnrollmentResource {
    private EnrollmentService enrollmentService;

    @PostMapping("/create")
    public EnrollmentResponseDto createEnrollment(@RequestHeader("Authorization") String token, @RequestBody @Valid EnrollmentRequestDto enrollmentRequestDto) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return enrollmentService.createEnrollment(enrollmentRequestDto, jwtToken);
    }

    @GetMapping("/current")
    public List<EnrollmentResponseDto> getCurrentUserEnrollments(@RequestHeader("Authorization") String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return enrollmentService.getCurrentUserEnrollments(jwtToken);
    }

    @GetMapping("/{id}")
    public EnrollmentResponseDto getEnrollment(@PathVariable Long id) {
        return enrollmentService.getEnrollment(id);
    }

    @GetMapping("/prof/all")
    public List<EnrollmentResponseDto> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    @GetMapping("/prof/{id}")
    public List<EnrollmentResponseDto> getAllEnrollmentsByUserId(@PathVariable Long id) {
        return enrollmentService.getAllEnrollmentsByUserId(id);
    }

    @GetMapping("/prof/course/{courseId}")
    public List<EnrollmentResponseDto> getAllEnrollmentsByCourseId(@RequestHeader("Authorization") String token,
                                                                   @PathVariable Long courseId) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return enrollmentService.getAllEnrollmentsByCourse(jwtToken, courseId);
    }

    @PatchMapping("/update/{id}")
    public EnrollmentResponseDto updateEnrollment(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long id,
                                                  @RequestBody @Valid EnrollmentUpdateDto enrollmentUpdateDto) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return enrollmentService.updateEnrollment(id, enrollmentUpdateDto, jwtToken);
    }
    @DeleteMapping("/{id}")
    public void deleteEnrollment(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        enrollmentService.deleteById(id, jwtToken);
    }
    @PatchMapping("/prof/setGpa/{id}")
    public EnrollmentResponseDto setGpa(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return enrollmentService.setGpa(id, jwtToken);
    }


}
