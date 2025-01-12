package com.ing.hubs.jpa.exception.enrollment;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class DuplicateEnrollmentException extends StoreException {
    public DuplicateEnrollmentException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("DUPLICATE_ENROLLMENT");
    }
    public DuplicateEnrollmentException(String message) {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("DUPLICATE_ENROLLMENT: " + message);
    }
}
