package com.ing.hubs.jpa.exception.class_activity;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class DuplicateActivityException extends StoreException {
    public DuplicateActivityException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("DUPLICATE_ACTIVITY: Student already has activity registered for this course");
    }
}
