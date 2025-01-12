package com.ing.hubs.jpa.resource;

import com.ing.hubs.jpa.dto.request.CourseUpdateDto;
import com.ing.hubs.jpa.dto.request.CourseRequestDto;
import com.ing.hubs.jpa.dto.response.CourseResponseDto;
import com.ing.hubs.jpa.service.CourseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/courses")
public class CourseResource {
    private CourseService courseService;

    @PostMapping("/create")
    public CourseResponseDto createCourse( @RequestHeader("Authorization") String jwtToken,
                                           @RequestBody @Valid CourseRequestDto courseRequestDto) {
        String token = jwtToken.replace("Bearer ", "");
        return courseService.createCourse(token, courseRequestDto);
    }

    @GetMapping("/all")
    public List<CourseResponseDto> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/professor/{professorId}")
    public List<CourseResponseDto> getProfessorCourses(@PathVariable Long professorId) {
        return courseService.getProfessorCourses(professorId);
    }

    @GetMapping("/{id}")
    public CourseResponseDto getCourse(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    @PatchMapping("/{id}")
    public CourseResponseDto updateCourse(@RequestHeader("Authorization") String jwtToken,
                                          @PathVariable Long id,
                                          @RequestBody @Valid CourseUpdateDto courseUpdateDto) {
        String token = jwtToken.replace("Bearer ", "");
        return courseService.updateCourse(token, id, courseUpdateDto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        courseService.delete(id);
    }
}
