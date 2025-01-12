package com.ing.hubs.jpa.exception.class_activity;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class MissingGradesException extends StoreException {
  public MissingGradesException(String message) {
    this.setHttpStatus(HttpStatus.NOT_FOUND);
    this.setMessage("MISSING_GRADES: " + message);
  }
}
