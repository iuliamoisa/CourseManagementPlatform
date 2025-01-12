package com.ing.hubs.jpa.exception.enrollment;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class EnrollmentNotFoundException extends StoreException {
    public EnrollmentNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("ENROLLMENT_NOT_FOUND");
    }
}
