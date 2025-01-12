package com.ing.hubs.jpa.exception.schedule;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class InvalidCourseDurationException extends StoreException {
    public InvalidCourseDurationException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("INVALID_COURSE_DURATION: Course duration must not be more than 2 hours");
    }
}
