package com.ing.hubs.jpa.exception.schedule;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class CourseDoesNotBelongToProfException extends StoreException {
    public CourseDoesNotBelongToProfException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("COURSE_DOES_NOT_BELONG_TO_PROF: Professor can't establish a schedule for a course that does not belong to them");
    }
}
