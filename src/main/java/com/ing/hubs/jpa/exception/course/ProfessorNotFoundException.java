package com.ing.hubs.jpa.exception.course;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class ProfessorNotFoundException extends StoreException {
    public ProfessorNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("PROFESSOR_NOT_FOUND");
    }
}
