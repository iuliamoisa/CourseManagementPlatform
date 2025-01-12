package com.ing.hubs.jpa.exception.schedule;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class ScheduleNotFoundException extends StoreException {
    public ScheduleNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("SCHEDULE_NOT_FOUND");
    }
}
