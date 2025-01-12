package com.ing.hubs.jpa.exception.user;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends StoreException {
  public UserAlreadyExistsException() {
    this.setHttpStatus(HttpStatus.BAD_REQUEST);
    this.setMessage("USER_ALREADY_EXISTS");
  }
    public UserAlreadyExistsException(String message) {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("USER_ALREADY_EXISTS: " + message);
    }
}
