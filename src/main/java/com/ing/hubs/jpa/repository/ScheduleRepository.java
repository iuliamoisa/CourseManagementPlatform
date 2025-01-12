package com.ing.hubs.jpa.repository;

import com.ing.hubs.jpa.entity.Schedule;
import com.ing.hubs.jpa.entity.enums.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query(value = "select * from schedule where course_id =  ?1", nativeQuery = true)
    List<Schedule> findAllByCourseId(Long courseId);

    @Query("SELECT s FROM Schedule s WHERE s.course.name = :courseName AND s.weekDay = :weekDay AND " +
            "(s.startTime < :endTime AND s.endTime > :startTime)")
    List<Schedule> findOverlappingSchedules(String courseName, WeekDay weekDay, LocalTime startTime, LocalTime endTime);

}
