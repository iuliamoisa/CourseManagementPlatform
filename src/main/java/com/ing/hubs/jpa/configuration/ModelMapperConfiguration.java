package com.ing.hubs.jpa.configuration;

import com.ing.hubs.jpa.dto.request.EnrollmentRequestDto;
import com.ing.hubs.jpa.dto.response.ClassActivityResponseDto;
import com.ing.hubs.jpa.dto.response.CourseResponseDto;
import com.ing.hubs.jpa.dto.response.EnrollmentResponseDto;
import com.ing.hubs.jpa.entity.ClassActivity;
import com.ing.hubs.jpa.entity.Course;
import com.ing.hubs.jpa.entity.Enrollment;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {
//    public ModelMapper modelMapper() {
//        return new ModelMapper();
//    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        mapper.typeMap(Course.class, CourseResponseDto.class).addMappings(m ->
                m.map(src -> src.getUser().getId(), CourseResponseDto::setProfessorId)
        );

        mapper.typeMap(Enrollment.class, EnrollmentResponseDto.class).addMappings(m -> {
            m.map(src -> src.getCourse().getId(), EnrollmentResponseDto::setCourseId);
            m.map(src -> src.getUser().getId(), EnrollmentResponseDto::setUserId);
        });

        mapper.typeMap(ClassActivity.class, ClassActivityResponseDto.class).addMappings(m-> {
            m.map(src -> src.getSchedule().getId(), ClassActivityResponseDto::setScheduleId);
            m.map(src -> src.getUser().getId(), ClassActivityResponseDto::setUserId);
        });
        return mapper;
    }
}
