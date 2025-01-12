package com.ing.hubs.jpa.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class StoreException extends RuntimeException {
  private HttpStatus httpStatus;
  private String message;

  public StoreException(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public StoreException(String message, HttpStatus httpStatus, String message1) {
    super(message);
    this.httpStatus = httpStatus;
    this.message = message1;
  }

}


