package com.ing.hubs.jpa.exception.schedule;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class OverlappingCoursesException extends StoreException {
    public OverlappingCoursesException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("OVERLAPPING_COURSES");
    }
    public OverlappingCoursesException(String message) {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("OVERLAPPING_COURSES: " + message);
    }
}
