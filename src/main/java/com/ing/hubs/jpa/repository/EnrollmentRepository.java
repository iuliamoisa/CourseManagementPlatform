package com.ing.hubs.jpa.repository;

import com.ing.hubs.jpa.entity.Enrollment;
import com.ing.hubs.jpa.entity.enums.EnrollmentStatus;
import com.ing.hubs.jpa.entity.enums.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findAllByCourseId(Long courseId);

    List<Enrollment> findAllByUserId(Long userId);

    @Query("""
    SELECT e FROM Enrollment e
    WHERE e.user.email = :email
""")
    List<Enrollment> findAllByEmail(@Param("email") String email);

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    @Query("""
    SELECT e FROM Enrollment e
    JOIN e.course.scheduleList s
    WHERE e.user.id = :userId
    AND s.weekDay = :weekDay
    AND (
        (s.startTime < :endTime AND s.endTime > :startTime)
        OR
        (s.startTime < :endTime AND s.endTime > :startTime)
    )

""")
    List<Enrollment> findOverlappingEnrollments(Long userId, WeekDay weekDay, LocalTime startTime, LocalTime endTime);
}
