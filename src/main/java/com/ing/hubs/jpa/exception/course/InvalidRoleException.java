package com.ing.hubs.jpa.exception.course;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class InvalidRoleException extends StoreException {
    public InvalidRoleException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("INVALID_ROLE");
    }
    public InvalidRoleException(String message) {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("INVALID_ROLE: " + message);
    }
}
