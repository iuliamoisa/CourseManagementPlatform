package com.ing.hubs.jpa.repository;

import com.ing.hubs.jpa.entity.ClassActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClassActivityRepository extends JpaRepository<ClassActivity, Long> {
    Optional<ClassActivity> findByScheduleIdAndUserId (Long scheduleId, Long userId);

    // Retrieve class activities for the student in this course
//    List<ClassActivity> activities = enrollment.getCourse()
//            .getScheduleList().stream()
//            .flatMap(schedule -> schedule.getClassActivities().stream()
//                    .filter(activity -> activity.getUser().getId().equals(enrollment.getUser().getId())))
//            .toList();
    List<ClassActivity> findByScheduleCourseIdAndUserId(Long courseId, Long userId);

    List<ClassActivity> findAllByUserIdAndScheduleIdIn(Long id, List<Long> scheduleIds);

    // get lab activities for the student in this course
    @Query("SELECT ca FROM ClassActivity ca JOIN Schedule s ON ca.schedule.id = s.id WHERE s.course.id = ?1 AND ca.user.id = ?2 AND s.courseType = 'LAB'")
    List<ClassActivity> findLabsByScheduleCourseIdAndUserId(Long courseId, Long userId);

    @Query("SELECT ca FROM ClassActivity ca JOIN Schedule s ON ca.schedule.id = s.id WHERE s.course.id = ?1 AND ca.user.id = ?2 AND s.courseType  = 'COURSE'")
    List<ClassActivity> findLCoursesByScheduleCourseIdAndUserId(Long courseId, Long userId);

}
