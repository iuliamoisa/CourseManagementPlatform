package com.ing.hubs.jpa.exception.enrollment;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class NotApprovedEnrollmentException extends StoreException {
    public NotApprovedEnrollmentException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("NOT_APPROVED_ENROLLMENT");
    }
    public NotApprovedEnrollmentException(String message) {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("NOT_APPROVED:" + message);
    }
}
