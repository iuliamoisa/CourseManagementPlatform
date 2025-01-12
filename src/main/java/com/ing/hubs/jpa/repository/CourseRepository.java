package com.ing.hubs.jpa.repository;

import com.ing.hubs.jpa.dto.response.CourseResponseDto;
import com.ing.hubs.jpa.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUserId(Long professorId);
}
