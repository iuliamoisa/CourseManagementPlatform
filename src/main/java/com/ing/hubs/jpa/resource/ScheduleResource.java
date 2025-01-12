package com.ing.hubs.jpa.resource;

import com.ing.hubs.jpa.dto.request.ScheduleRequestDto;
import com.ing.hubs.jpa.dto.request.ScheduleUpdateDto;
import com.ing.hubs.jpa.dto.response.ScheduleResponseDto;
import com.ing.hubs.jpa.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/schedule")
public class ScheduleResource {
    private ScheduleService scheduleService;

    @PostMapping
    public ScheduleResponseDto createSchedule(@RequestHeader("Authorization") String token, @RequestBody @Valid ScheduleRequestDto scheduleRequestDto) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return scheduleService.createSchedule(scheduleRequestDto, jwtToken); //prof
    }

    @GetMapping("/all")
    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
    } //prof

    @PatchMapping("/update/{id}")
    public ScheduleResponseDto updateSchedule(@PathVariable Long id, @RequestBody @Valid ScheduleUpdateDto scheduleUpdateDto) {
        return scheduleService.updateSchedule(id, scheduleUpdateDto); //prof
    }

    @GetMapping("/course/{id}")
    public List<ScheduleResponseDto> getSchedulesByCourse(@PathVariable Long id) {
        return scheduleService.getSchedulesByCourse(id);
    }
}
