package com.ing.hubs.jpa.exception.class_activity;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class ClassActivityNotFoundException extends StoreException {
    public ClassActivityNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("CLASS_ACTIVITY_NOT_FOUND");
    }
    public ClassActivityNotFoundException(String message) {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("CLASS_ACTIVITY_NOT_FOUND: " + message);
    }
}
