package com.ing.hubs.jpa.exception.course;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class CourseNotFoundException extends StoreException {
    public CourseNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("COURSE_NOT_FOUND");
    }
}
