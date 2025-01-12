package com.ing.hubs.jpa.exception.schedule;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class InvalidDateException extends StoreException {
    public InvalidDateException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("INVALID_DATE: Start date must be before end date and both must be in the future");
    }
}
