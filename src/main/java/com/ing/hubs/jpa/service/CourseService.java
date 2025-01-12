package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.CourseUpdateDto;
import com.ing.hubs.jpa.dto.request.CourseRequestDto;
import com.ing.hubs.jpa.dto.response.CourseResponseDto;
import com.ing.hubs.jpa.entity.Course;
import com.ing.hubs.jpa.entity.enums.UserRole;
import com.ing.hubs.jpa.exception.course.CourseNotFoundException;
import com.ing.hubs.jpa.exception.course.InvalidRoleException;
import com.ing.hubs.jpa.repository.CourseRepository;
import com.ing.hubs.jpa.repository.UserRepository;
import com.ing.hubs.jpa.security.webToken.JwtService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.ing.hubs.jpa.exception.course.ProfessorNotFoundException;
import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {
    private CourseRepository courseRepository;
    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private JwtService jwtService;

    public CourseResponseDto createCourse(String jwtToken, CourseRequestDto courseRequestDto) {
        var professor = userRepository.findById(courseRequestDto.getProfessorId())
                .orElseThrow(ProfessorNotFoundException::new);
        if (professor.getUserRole() != UserRole.PROFESSOR) {
            throw new InvalidRoleException();
        }
        if(!jwtService.extractEmail(jwtToken).equals(professor.getEmail())){
            throw new RuntimeException("You can't create a course on behalf of other professors");
        }
        var course = modelMapper.map(courseRequestDto, Course.class);
        course.setUser(professor);
        course.setId(null);
        var savedEntity = courseRepository.save(course);
        return modelMapper.map(savedEntity, CourseResponseDto.class);
    }

    public List<CourseResponseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(course -> {
                    CourseResponseDto responseDto = modelMapper.map(course, CourseResponseDto.class);
                    if (course.getUser() != null) {
                        responseDto.setProfessorId(course.getUser().getId());
                    }
                    return responseDto;
                })
                .toList();
    }

    public List<CourseResponseDto> getProfessorCourses(Long professorId) {
        var professor = userRepository.findById(professorId)
                .orElseThrow(ProfessorNotFoundException::new);
        if (professor.getUserRole() != UserRole.PROFESSOR) {
            throw new InvalidRoleException();
        }
        return courseRepository.findByUserId(professorId).stream()
                .map(course -> {
                    CourseResponseDto responseDto = modelMapper.map(course, CourseResponseDto.class);
                    if (course.getUser() != null) {
                        responseDto.setProfessorId(course.getUser().getId());
                    }
                    return responseDto;
                })
                .toList();
    }

    public Course loadCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(CourseNotFoundException::new);
    }
    public CourseResponseDto getCourse(Long id) {
        var course = loadCourse(id);
        var responseDto = modelMapper.map(course, CourseResponseDto.class);
        if (course.getUser() != null) {
            responseDto.setProfessorId(course.getUser().getId());
        }
        return responseDto;
    }

    public CourseResponseDto updateCourse(String jwtToken, Long id, CourseUpdateDto courseUpdateDto){
        var course = loadCourse(id);
        if(!jwtService.extractEmail(jwtToken).equals(course.getUser().getEmail())){
            throw new RuntimeException("You can't update a course on behalf of other professors");
        }
        if(courseUpdateDto.getDescription() != null){
            course.setDescription(courseUpdateDto.getDescription());
        }
        if(courseUpdateDto.getMaxAttendees() > 0){
            course.setMaxAttendees(courseUpdateDto.getMaxAttendees());
        }
        if(courseUpdateDto.getName() != null){
            course.setName(courseUpdateDto.getName());
        }
        var savedEntity = courseRepository.save(course);
        return modelMapper.map(savedEntity, CourseResponseDto.class);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }
}
