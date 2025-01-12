package com.ing.hubs.jpa.exception.enrollment;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class MaxAttendeesExceededException extends StoreException {
    public MaxAttendeesExceededException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("MAX_ATTENDEES_EXCEEDED: The maximum number of attendees has been exceeded");
    }
}
