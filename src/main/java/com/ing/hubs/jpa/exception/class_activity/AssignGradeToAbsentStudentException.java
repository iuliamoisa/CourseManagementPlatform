package com.ing.hubs.jpa.exception.class_activity;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class AssignGradeToAbsentStudentException extends StoreException {
     public AssignGradeToAbsentStudentException(){
       this.setHttpStatus(HttpStatus.BAD_REQUEST);
         this.setMessage("ASSIGN_GRADE_TO_ABSENT_STUDENT: Cannot assign grade to absent student");
     }
}
