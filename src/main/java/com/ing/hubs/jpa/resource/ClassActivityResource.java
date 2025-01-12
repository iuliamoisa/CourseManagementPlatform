package com.ing.hubs.jpa.resource;

import com.ing.hubs.jpa.dto.request.ClassActivityRequestDto;
import com.ing.hubs.jpa.dto.request.ClassActivityUpdateDto;
import com.ing.hubs.jpa.dto.response.ClassActivityResponseDto;
import com.ing.hubs.jpa.service.ClassActivityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/class-activity")
public class ClassActivityResource {
    private ClassActivityService classActivityService;

    @PostMapping
    public ClassActivityResponseDto createActivity(@RequestBody @Valid ClassActivityRequestDto classActivityRequestDto) {
        return classActivityService.createActivity(classActivityRequestDto);
    }

    @GetMapping("/activity/{id}")
    public ClassActivityResponseDto getActivity(@PathVariable Long id) {
        return classActivityService.getActivity(id);
    }

    @GetMapping("/activity/all")
    public List<ClassActivityResponseDto> getUserActivitiesForCourse(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam Long courseId) {
        String token = jwtToken.replace("Bearer ", "");
        return classActivityService.getUserActivitiesForCourse(token, courseId);
    }

    @PatchMapping("/{id}")
    public ClassActivityResponseDto updateActivity(@PathVariable Long id, @RequestBody @Valid ClassActivityUpdateDto classActivityUpdateDto){
        return classActivityService.updateActivity(id, classActivityUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable Long id){
        classActivityService.deleteActivity(id);
    }
}
