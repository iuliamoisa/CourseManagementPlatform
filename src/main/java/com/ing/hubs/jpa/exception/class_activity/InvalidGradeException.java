package com.ing.hubs.jpa.exception.class_activity;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class InvalidGradeException extends StoreException {
    public InvalidGradeException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("INVALID_GRADE: Grade must be between 1.0 and 10.0");
    }
}
