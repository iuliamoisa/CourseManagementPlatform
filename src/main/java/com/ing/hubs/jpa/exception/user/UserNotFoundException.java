package com.ing.hubs.jpa.exception.user;

import com.ing.hubs.jpa.exception.StoreException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends StoreException {
  public UserNotFoundException() {
    this.setHttpStatus(HttpStatus.NOT_FOUND);
    this.setMessage("USER_NOT_FOUND");
  }
  public UserNotFoundException(String message) {
    this.setHttpStatus(HttpStatus.NOT_FOUND);
    this.setMessage("USER_NOT_FOUND: " + message + " does not exist");
  }
}

