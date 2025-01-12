package com.ing.hubs.jpa.exception.schedule;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class InvalidTimeException extends StoreException {
    public InvalidTimeException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("INVALID_TIME: Start time must be before end time");
    }
}
