package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.ClassActivityRequestDto;
import com.ing.hubs.jpa.dto.request.ClassActivityUpdateDto;
import com.ing.hubs.jpa.dto.response.ClassActivityResponseDto;
import com.ing.hubs.jpa.dto.response.EnrollmentResponseDto;
import com.ing.hubs.jpa.entity.ClassActivity;
import com.ing.hubs.jpa.entity.Schedule;
import com.ing.hubs.jpa.entity.User;
import com.ing.hubs.jpa.entity.enums.AttendanceStatus;
import com.ing.hubs.jpa.entity.enums.UserRole;
import com.ing.hubs.jpa.exception.class_activity.AssignGradeToAbsentStudentException;
import com.ing.hubs.jpa.exception.class_activity.ClassActivityNotFoundException;
import com.ing.hubs.jpa.exception.class_activity.DuplicateActivityException;
import com.ing.hubs.jpa.exception.class_activity.InvalidGradeException;
import com.ing.hubs.jpa.exception.course.InvalidRoleException;
import com.ing.hubs.jpa.exception.schedule.ScheduleNotFoundException;
import com.ing.hubs.jpa.exception.user.UserNotFoundException;
import com.ing.hubs.jpa.repository.*;
import com.ing.hubs.jpa.security.webToken.JwtService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClassActivityService {
    private ClassActivityRepository classActivityRepository;
    private ModelMapper modelMapper;
    private ScheduleRepository scheduleRepository;
    private UserRepository studentRepository;
    private JwtService jwtService;

    public ClassActivity loadActivity(Long id){
        return classActivityRepository.findById(id)
                .orElseThrow(ClassActivityNotFoundException::new);
    }

    public ClassActivityResponseDto getActivity(Long id){
        var activity = loadActivity(id);
        return modelMapper.map(activity, ClassActivityResponseDto.class);
    }

    public ClassActivityResponseDto createActivity(ClassActivityRequestDto classActivityRequestDto) {
        var schedule = scheduleRepository.findById(classActivityRequestDto.getScheduleId())
                .orElseThrow(() -> new ClassActivityNotFoundException("Activity not found on schedule"));
        var student = studentRepository.findById(classActivityRequestDto.getUserId())
                .orElseThrow(UserNotFoundException::new);
        if (student.getUserRole() != UserRole.STUDENT) {
            throw new InvalidRoleException("The user is not a student");
        }
        if (classActivityRequestDto.getAttendanceStatus() == AttendanceStatus.ABSENT
                && classActivityRequestDto.getGrade() > 0) {
            throw new AssignGradeToAbsentStudentException();
        }
        ClassActivity activity = new ClassActivity();
        activity.setUser(student);
        activity.setSchedule(schedule);
        activity.setGrade(classActivityRequestDto.getGrade());
        activity.setAttendanceStatus(classActivityRequestDto.getAttendanceStatus());
        var savedEntity = classActivityRepository.save(activity);
        return modelMapper.map(savedEntity, ClassActivityResponseDto.class);
    }

    public ClassActivityResponseDto updateActivity(Long id, ClassActivityUpdateDto classActivityUpdateDto){
            var activity = loadActivity(id);

            if (classActivityUpdateDto.getGrade() < 1.0 || classActivityUpdateDto.getGrade() > 10.0) {
                throw new InvalidGradeException();
            } else {
                activity.setGrade(classActivityUpdateDto.getGrade());
            }
            if(classActivityUpdateDto.getAttendanceStatus() != null){
                activity.setAttendanceStatus(classActivityUpdateDto.getAttendanceStatus());
            }
            activity.setUpdatedAt(classActivityUpdateDto.getUpdatedAt());
            var updatedACtivity = classActivityRepository.save(activity);
            return modelMapper.map(updatedACtivity, ClassActivityResponseDto.class);

    }

    public void deleteActivity(Long id){
        classActivityRepository.deleteById(id);
    }

    public List<ClassActivityResponseDto> getUserActivitiesForCourse(String jwtToken, Long courseId) {
        String emailFromJwt = jwtService.extractEmail(jwtToken);
        User user = studentRepository.findByEmail(emailFromJwt)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Schedule> courseSchedules = scheduleRepository.findAllByCourseId(courseId);
        if (courseSchedules.isEmpty()) {
            throw new ScheduleNotFoundException();
        }
        List<Long> scheduleIds = courseSchedules.stream()
                .map(Schedule::getId)
                .toList();
        List<ClassActivity> activities = classActivityRepository.findAllByUserIdAndScheduleIdIn(user.getId(), scheduleIds);
        return activities.stream()
                .map(activity -> {
                    ClassActivityResponseDto responseDto = modelMapper.map(activity, ClassActivityResponseDto.class);
                    if (activity.getSchedule() != null) {
                        responseDto.setScheduleId(activity.getSchedule().getId());
                    }
                    if (activity.getUser() != null) {
                        responseDto.setUserId(activity.getUser().getId());
                    }
                    return responseDto;
                })
                .toList();
    }
}
